
document.addEventListener('DOMContentLoaded', function() {
   
    const profileForm = document.getElementById('profileForm');
    if (profileForm) {
        profileForm.addEventListener('submit', handleProfileSubmit);
    }
    
   
    initializeBMICalculator();
    
  
    const userTargets = JSON.parse(localStorage.getItem('userTargets') || '{}');
    if (userTargets.adjustedTdee) {
        document.getElementById('adjustedTdee').textContent = userTargets.adjustedTdee;
    }
    if (userTargets.waterTarget) {
        document.getElementById('water').textContent = userTargets.waterTarget;
    }
});


function initializeBMICalculator() {
    const heightInput = document.getElementById('height');
    const weightInput = document.getElementById('weight');
    const bmiDisplay = document.getElementById('bmi');
    
    function calculateBMI() {
        if (heightInput.value && weightInput.value) {
            const height = parseFloat(heightInput.value) / 100; 
            const weight = parseFloat(weightInput.value);
            const bmi = weight / (height * height);
            bmiDisplay.value = bmi.toFixed(2);
        }
    }
    
    heightInput?.addEventListener('input', calculateBMI);
    weightInput?.addEventListener('input', calculateBMI);
}
function updateProfile() {
    const profileData = {
        gender: document.getElementById('gender').value,
        weight: parseFloat(document.getElementById('weight').value),
        height: parseFloat(document.getElementById('height').value),
        age: parseInt(document.getElementById('age').value),
        activityLevel: parseFloat(document.getElementById('activityLevel').value)
    };

    fetch('/api/user/profile', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(profileData)
    })
    .then(response => response.json())
    .then(data => {
       
        const tdee = data.tdee;
        const waterTarget = data.waterTarget;
        
   
        const calculationResults = document.querySelector('.calculation-results');
        if (calculationResults) {
            calculationResults.innerHTML = `
                <h3>計算結果</h3>
                <p>每日總熱量消耗 (TDEE)：${tdee} 大卡</p>
                <p>建議飲水量：${waterTarget} ml</p>
            `;
        }
    })
    .catch(error => console.error('Error:', error));
}


function updateCalculationResults(tdee, waterTarget) {
    const calculationResults = document.querySelector('.calculation-results');
    if (calculationResults) {
        calculationResults.innerHTML = `
            <h3>計算結果</h3>
            <p>基礎代謝率 (TDEE)：${Math.round(tdee)} 大卡</p>
            <p>建議飲水量：${waterTarget} ml</p>
        `;
    }
}


async function handleProfileSubmit(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const profileData = {
        userId: document.getElementById('userId').value,
        gender: formData.get('gender'),
        age: parseInt(formData.get('age')),
        height: parseFloat(formData.get('height')),
        weight: parseFloat(formData.get('weight')),
        activityLevel: formData.get('activityLevel'),
        tdee: parseInt(document.getElementById('tdee').value),
        waterTarget: parseInt(document.getElementById('waterTarget').value),
        bmi: parseFloat(document.getElementById('bmi').value)
    };

    try {
        const response = await fetch('/api/user/profile', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(profileData)
        });

        if (response.ok) {
            alert('個人資料更新成功！');
            window.location.href = '/daily-record';
        } else {
            const error = await response.json();
            alert('更新失敗：' + error.message);
        }
    } catch (error) {
        console.error('更新錯誤:', error);
        alert('更新過程中發生錯誤');
    }
}


function confirmAndRedirect() {
    if (confirm('確定要離開此頁面嗎？未保存的更改將會遺失。')) {
        window.location.href = '/daily-record';
    }
}