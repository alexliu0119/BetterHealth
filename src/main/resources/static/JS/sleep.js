document.addEventListener('DOMContentLoaded', function() {
    const sleepForm = document.getElementById('sleepForm');
    sleepForm.addEventListener('submit', recordSleep);
    loadTodaySleep();
});

async function recordSleep(event) {
    event.preventDefault();
    
    const userId = document.getElementById('userId').value;
    const sleepStart = new Date(document.getElementById('sleepStart').value);
    const sleepEnd = new Date(document.getElementById('sleepEnd').value);
    const deepSleepMinutes = parseInt(document.getElementById('deepSleepMinutes').value);
    
    try {
        const response = await fetch(`/api/sleep/${userId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                sleepStart: sleepStart.getTime(),
                sleepEnd: sleepEnd.getTime(),
                deepSleepMinutes: deepSleepMinutes
            })
        });

        if (!response.ok) {
            throw new Error('保存失敗');
        }

        const result = await response.json();
        await loadTodaySleep();
        sleepForm.reset();
    } catch (error) {
        console.error('Error:', error);
        alert('記錄睡眠失敗，請稍後重試');
    }
}

function displaySleepRecord(record) {
    const container = document.getElementById('sleepHistory');
    if (!container) {
        console.error('Sleep history container not found');
        return;
    }

   
    if (!record || record.totalSleepHours === undefined) {
        container.innerHTML = `
            <div class="card">
                <div class="card-body">
                    <div class="alert alert-info mb-0">
                        <i class="fas fa-info-circle"></i> 尚未得到今日睡眠數據
                    </div>
                </div>
            </div>
        `;
        return;
    }

    const sleepQualityEmoji = {
        'GOOD': '😊',
        'FAIR': '😐',
        'POOR': '😴'
    };

    const totalHours = record.totalSleepHours ? record.totalSleepHours.toFixed(1) : '0.0';
    const deepSleepPercent = record.deepSleepMinutes && record.totalSleepHours ? 
        ((record.deepSleepMinutes / (totalHours * 60)) * 100).toFixed(1) : '0.0';

    container.innerHTML = `
        <div class="card">
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <h6>總睡眠時間</h6>
                        <p class="h4">${totalHours} 小時</p>
                    </div>
                    <div class="col-md-6">
                        <h6>深度睡眠</h6>
                        <p class="h4">${record.deepSleepMinutes || 0} 分鐘 (${deepSleepPercent}%)</p>
                    </div>
                </div>
                <div class="mt-3">
                    <h6>睡眠品質</h6>
                    <p class="h4">${sleepQualityEmoji[record.sleepQuality] || '😴'} ${record.sleepQuality || '尚未評估'}</p>
                </div>
            </div>
        </div>
    `;
}
async function clearTodaySleep() {
    try {
        const userId = document.getElementById('userId').value;
        const response = await fetch(`/api/sleep/today/${userId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('刪除失敗');
        }

       
        document.getElementById('sleepHistory').innerHTML = `
            <div class="alert alert-info mb-0">
                <i class="fas fa-info-circle"></i> 尚未得到今日睡眠數據
            </div>
        `;
        
       
        document.getElementById('sleepForm').reset();
        
    } catch (error) {
        console.error('Error:', error);
        alert('清除記錄失敗，請稍後重試');
    }
}

async function loadTodaySleep() {
    try {
        const userId = document.getElementById('userId').value;
        const response = await fetch(`/api/sleep/today/${userId}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        console.log('API Response:', data); 
        displaySleepRecord(data);
        
    } catch (error) {
        console.error('Error:', error);
        const container = document.getElementById('sleepHistory');
        if (container) {
            container.innerHTML = `
                <div class="card">
                    <div class="card-body">
                        <div class="alert alert-info mb-0">
                            尚未得到今日睡眠數據
                        </div>
                    </div>
                </div>
            `;
        }
    }
}