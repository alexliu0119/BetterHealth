document.addEventListener('DOMContentLoaded', function() {
    const userIdElement = document.getElementById('userId');
    if (!userIdElement) {
        console.error('未找到用戶ID元素');
        return;
    }
	  const userId = userIdElement.value;
	    if (!userId) {
	        console.error('用戶ID為空');
	        return;
	    }
	    
	    loadHealthScore(userId);
	  
	});
	async function loadHealthScore(userId) {
	    try {
	        console.log('開始加載健康評分數據，用戶ID:', userId);
	        
	       
	        const response = await fetch(`/api/health-scores/today/${userId}`);
	        if (!response.ok) {
	            throw new Error(`獲取今日評分失敗: ${response.statusText}`);
	        }
	        const scores = await response.json();
	        console.log('今日評分數據:', scores);
	        updateScoreDisplays(scores);
	        
	        
	        console.log('開始獲取週趨勢數據');
	        const weeklyResponse = await fetch(`/api/health-scores/weekly-trend/${userId}`);
	        if (!weeklyResponse.ok) {
	            throw new Error(`獲取週趨勢數據失敗: ${weeklyResponse.statusText}`);
	        }
	        const weeklyData = await weeklyResponse.json();
	        console.log('週趨勢數據:', weeklyData);
	        
	        if (weeklyData && weeklyData.length > 0) {
	            updateWeeklyChart(weeklyData);
	            updateHistoryTable(weeklyData);
	        } else {
	            console.log('沒有週趨勢數據');
	        }
	    } catch (error) {
	        console.error('加載數據失敗:', error);
	        showError('加載健康評分數據失敗，請稍後重試');
	    }
	}
	
			function updateScoreDisplays(scores) {
			    try {
			     
			        const elements = {
			            'todayScore': scores.totalScore,
			            'bmiScore': scores.bmiScore,
			            'bloodSugarScore': scores.bloodSugarScore,
			            'dietScore': scores.dietScore,
			            'exerciseScore': scores.exerciseScore,
			            'sleepScore': scores.sleepScore,
			            'waterScore': scores.waterScore
			        };

			        for (const [id, value] of Object.entries(elements)) {
			            const element = document.getElementById(id);
			            if (element) {
			                element.textContent = value;
			            } else {
			                console.error(`找不到元素: ${id}`);
			            }
			        }
			        
			        
			        const progressBar = document.getElementById('todayScoreBar');
			        if (progressBar) {
			            progressBar.style.width = `${scores.totalScore}%`;
			            progressBar.setAttribute('aria-valuenow', scores.totalScore);
			        }
			    } catch (error) {
			        console.error('更新分數顯示時出錯:', error);
			    }
			}

    function initializeCharts() {
       
        const ctx = document.getElementById('healthScoreChart').getContext('2d');
        new Chart(ctx, {
            type: 'radar',
            data: {
                labels: ['BMI', '血糖', '飲食', '運動', '睡眠'],
                datasets: [{
                    label: '健康評分',
                    data: [0, 0, 0, 0, 0],
                    backgroundColor: 'rgba(54, 162, 235, 0.2)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1
                }]
            }
        });
    }

    function updateCharts(scores) {
        const chart = Chart.getChart('healthScoreChart');
        if (chart) {
            chart.data.datasets[0].data = [
                scores.bmiScore || 0,
                scores.bloodSugarScore || 0,
                scores.dietScore || 0,
                scores.exerciseScore || 0,
                scores.sleepScore || 0
            ];
            chart.update();
        }
    }

	function updateWeeklyChart(weeklyData) {
    const ctx = document.getElementById('weeklyTrendChart').getContext('2d');
    
   
    const dates = weeklyData.map(score => {
        const date = new Date(score.recordDate);
        return date.toLocaleDateString('zh-TW');
    });
    
    const scores = weeklyData.map(score => score.totalScore);
    
   
    if (window.weeklyChart) {
        window.weeklyChart.destroy();
    }
    
    window.weeklyChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: dates,
            datasets: [{
                label: '健康總分',
                data: scores,
                borderColor: 'rgb(75, 192, 192)',
                tension: 0.1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100
                }
            }
        }
    });
}



   function updateHistoryTable(historyData) {
    const tableBody = document.querySelector('#historyTable tbody');
    if (!tableBody) return;
    
    tableBody.innerHTML = '';
    
    historyData.forEach(record => {
        const row = document.createElement('tr');
        const date = new Date(record.recordDate).toLocaleDateString('zh-TW');
        
        row.innerHTML = `
            <td>${date}</td>
            <td>${record.totalScore}</td>
            <td>${record.bmiScore}</td>
            <td>${record.bloodSugarScore}</td>
            <td>${record.dietScore}</td>
            <td>${record.exerciseScore}</td>
            <td>${record.sleepScore}</td>
            <td>${record.waterScore}</td>
        `;
        
        tableBody.appendChild(row);
    });
}

    function showHealthSuggestions(suggestions) {
        const container = document.getElementById('healthSuggestions');
        container.innerHTML = '';
        
        suggestions.forEach(suggestion => {
            const div = document.createElement('div');
            div.className = 'alert alert-info';
            div.textContent = suggestion;
            container.appendChild(div);
        });
    }

    function updateScoreLevel(totalScore) {
        let level = '';
        let color = '';
        
        if (totalScore >= 90) {
            level = '優秀';
            color = 'text-success';
        } else if (totalScore >= 80) {
            level = '良好';
            color = 'text-primary';
        } else if (totalScore >= 70) {
            level = '一般';
            color = 'text-warning';
        } else {
            level = '需要改善';
            color = 'text-danger';
        }
        
        const scoreLevelElement = document.getElementById('scoreLevel');
        scoreLevelElement.textContent = level;
        scoreLevelElement.className = `score-value ${color}`;
    }
	
	function showError(message) {
	    try {
	        const container = document.querySelector('.container');
	        if (container) {
	            const errorDiv = document.createElement('div');
	            errorDiv.className = 'alert alert-danger mt-3';
	            errorDiv.textContent = message;
	            container.insertBefore(errorDiv, container.firstChild);
	        }
	    } catch (error) {
	        console.error('顯示錯誤訊息時出錯:', error);
	    }
	}

    
    async function apiRequest(url) {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`API請求錯誤: ${response.statusText}`);
        }
        return response.json();
    }

  
    function handleError(error) {
        console.error(error);
        alert('發生錯誤，請稍後再試');
    }

