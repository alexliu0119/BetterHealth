
document.addEventListener('DOMContentLoaded', async function() {
    await loadUserProfile();
    loadExerciseHistory();
    
  
    document.getElementById('exerciseDate').valueAsDate = new Date();
});


async function loadUserProfile() {
    try {
        const userId = document.getElementById('userId').value;
        
        const response = await fetch(`/api/users/${userId}`);
        if (!response.ok) {
            throw new Error('獲取用戶資料失敗');
        }
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('獲取用戶資料錯誤:', error);
    }
}



function calculateHeartRateRanges(age) {
    if (!age) {
        return;
    }
    
    const maxHeartRate = 220 - age;
    
   
    const ranges = {
        warmUp: Math.round(maxHeartRate * 0.5),
        fatBurn: Math.round(maxHeartRate * 0.6),
        cardio: Math.round(maxHeartRate * 0.7),
        peak: Math.round(maxHeartRate * 0.85),
        max: maxHeartRate
    };
    
    
    const lightMin = ranges.warmUp;
    const lightMax = ranges.fatBurn;
    const moderateMin = ranges.fatBurn;
    const moderateMax = ranges.cardio;
    const vigorousMin = ranges.cardio;
    const vigorousMax = ranges.peak;

    
    const rangesDiv = document.getElementById('personalHeartRateRanges');
    if (rangesDiv) {
        rangesDiv.innerHTML = `
            <h6>您的個人化心率範圍：</h6>
            <ul class="list-unstyled mb-0">
                <li>💚 輕度運動: ${lightMin}-${lightMax} 下/分 (最大心率的 50-60%)</li>
                <li>💛 中度運動: ${moderateMin}-${moderateMax} 下/分 (最大心率的 60-70%)</li>
                <li>❤️ 高強度運動: ${vigorousMin}-${vigorousMax} 下/分 (最大心率的 70-85%)</li>
            </ul>
            <small class="mt-2 d-block text-muted">
                ⚠️ 注意：如果您有心臟病史或其他健康問題，請先諮詢醫生建議的運動強度
            </small>
        `;
        rangesDiv.style.display = 'block';
    }
}


document.getElementById('age').addEventListener('input', function() {
    const age = parseInt(this.value);
    calculateHeartRateRanges(age);
});




function getMET(exerciseType, intensity) {
    const metValues = {
        '跑步': {
            'LOW': 7.0,    
            'MEDIUM': 10.0, 
            'HIGH': 12.5    
        },
        '游泳': {
            'LOW': 6.0,    
            'MEDIUM': 8.3,  
            'HIGH': 10.0    
        },
        '騎車': {
            'LOW': 4.0,    
            'MEDIUM': 8.0,  
            'HIGH': 10.0    
        },
        '健身': {
            'LOW': 3.5,    
            'MEDIUM': 5.0,  
            'HIGH': 6.0    
        },
        '走路': {
            'LOW': 2.5,    
            'MEDIUM': 3.5,  
            'HIGH': 4.5    
        }
    };

    return metValues[exerciseType]?.[intensity] || 4.0;
}


function calculateCalories() {
    const exerciseType = document.getElementById('exerciseType').value;
    const intensity = document.getElementById('intensity').value;
    const duration = parseInt(document.getElementById('duration').value) || 0;
    const weight = parseFloat(document.getElementById('userWeight').value) || 60;

    if (!exerciseType || !intensity || !duration) return;

    const met = getMET(exerciseType, intensity);
    const hours = duration / 60;
    const calories = Math.round(met * weight * hours);

    document.getElementById('caloriesBurned').value = calories;
}


async function loadExerciseHistory() {
    try {
        const today = new Date().toISOString().split('T')[0];
        const userId = document.getElementById('userId').value;
        const response = await fetch(`/api/exercise/user/${userId}/today`);
        
        if (!response.ok) {
            throw new Error('獲取運動記錄失敗');
        }
        
        const exercises = await response.json();
        const historyContainer = document.getElementById('exerciseHistory');
        historyContainer.innerHTML = '<h3>今日運動記錄</h3>';
        
        if (exercises.length === 0) {
            historyContainer.innerHTML += '<p>今天還沒有運動記錄</p>';
            return;
        }

        exercises.forEach(exercise => {
            const exerciseElement = document.createElement('div');
            exerciseElement.className = 'exercise-record';
            exerciseElement.innerHTML = `
                <p>日期: ${new Date(exercise.exerciseDate).toLocaleDateString()}</p>
                <p>運動類型: ${exercise.exerciseType}</p>
                <p>持續時間: ${exercise.durationMinutes}分鐘</p>
                
                <p>消耗卡路里: ${exercise.caloriesBurned}大卡</p>
            `;
            historyContainer.appendChild(exerciseElement);
        });
    } catch (error) {
        console.error('Error:', error);
    }
}

function displayExerciseHistory(exercises) {
    const historyContainer = document.getElementById('exerciseHistory');
    historyContainer.innerHTML = '';

    if (!exercises || exercises.length === 0) {
        historyContainer.innerHTML = '<p class="text-muted">今日尚無運動記錄</p>';
        return;
    }

    exercises.forEach(record => {
        const exerciseElement = document.createElement('div');
        exerciseElement.innerHTML = `
            <div class="card mb-3">
                <div class="card-body">
                    <h5 class="card-title">${record.exerciseType}</h5>
                    <p>強度: ${record.intensity}</p>
                    <p>持續時間: ${record.durationMinutes} 分鐘</p>
                    <p>消耗卡路里: ${record.caloriesBurned} 大卡</p>
                </div>
            </div>
        `;
        historyContainer.appendChild(exerciseElement);
    });
}


async function submitExercise(event) {
    event.preventDefault();
    try {
        const form = event.target;
        const exerciseData = {
            userId: document.getElementById('userId').value,
            exerciseType: form.exerciseType.value,
            durationMinutes: parseInt(form.duration.value),
            intensity: form.intensity.value,
            caloriesBurned: parseInt(document.getElementById('caloriesBurned').value),
			exerciseDate: document.getElementById('exerciseDate').value  // 使用表單中選擇的日期
			     };


       
        console.log('提交的運動數據:', exerciseData);

        const response = await fetch('/api/exercise', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(exerciseData)
        });

        if (!response.ok) {
            const errorData = await response.text();
            throw new Error(errorData || '記錄運動失敗');
        }

		const savedExercise = await response.json();
		console.log('Saved exercise:', savedExercise);  
        alert('運動記錄成功!');
        form.reset();
        loadExerciseHistory();
    } catch (error) {
        console.error('Error:', error);
        alert(error.message);
    }
}
async function clearTodayExercise() {
    try {
        const userId = document.getElementById('userId').value;
        const response = await fetch(`/api/exercise/today/${userId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('刪除失敗');
        }

     
        document.getElementById('exerciseHistory').innerHTML = `
            <div class="alert alert-info mb-0">
                <i class="fas fa-info-circle"></i> 尚未記錄今日運動
            </div>
        `;
        
        
        if (document.getElementById('exerciseForm')) {
            document.getElementById('exerciseForm').reset();
        }
        
    } catch (error) {
        console.error('Error:', error);
        alert('清除記錄失敗，請稍後重試');
    }
}

document.getElementById('exerciseForm').addEventListener('submit', submitExercise);
document.getElementById('exerciseType').addEventListener('change', calculateCalories);
document.getElementById('intensity').addEventListener('change', calculateCalories);
document.getElementById('duration').addEventListener('input', calculateCalories);