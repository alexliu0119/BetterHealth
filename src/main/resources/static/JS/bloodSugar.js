document.addEventListener('DOMContentLoaded', function() {
    
    loadTodayBloodSugar();
    
    
    const form = document.getElementById('bloodSugarForm');
    if (form) {
        form.addEventListener('submit', recordBloodSugar);
    }
    
  
    const deleteButton = document.getElementById('deleteButton');
    if (deleteButton) {
        deleteButton.addEventListener('click', deleteTodayRecord);
    }
});

async function recordBloodSugar(event) {
    event.preventDefault();
    
    const bloodSugarInput = document.getElementById('bloodSugar');
    const userIdInput = document.getElementById('userId');
    
    if (!bloodSugarInput || !userIdInput) {
        alert('無法找到必要的表單元素');
        return;
    }
    
    const bloodSugarValue = bloodSugarInput.value;
    const userId = userIdInput.value;
    
    if (!bloodSugarValue || !userId) {
        alert('請填寫完整的血糖資訊');
        return;
    }

    const data = {
        bloodSugar: parseFloat(bloodSugarValue),
        user: {
            userId: parseInt(userId)
        }
    };

    try {
        const response = await fetch('/api/blood-sugar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        const responseData = await response.json();

        if (!response.ok) {
            throw new Error(responseData.message || '記錄失敗');
        }

        alert('血糖記錄成功！');
        bloodSugarInput.value = ''; 
        loadTodayBloodSugar();
    } catch (error) {
        console.error('Error:', error);
        alert(error.message);
    }
}

async function loadTodayBloodSugar() {
    const userIdInput = document.getElementById('userId');
    const bloodSugarDisplay = document.getElementById('todayBloodSugar');
    const deleteButton = document.getElementById('deleteButton');
    
    if (!userIdInput || !bloodSugarDisplay) {
        console.error('無法找到必要的顯示元素');
        return;
    }

    try {
        const response = await fetch(`/api/blood-sugar/today/${userIdInput.value}`);
        const data = await response.json();

        if (data.message) {
            bloodSugarDisplay.textContent = data.message;
            if (deleteButton) deleteButton.style.display = 'none';
        } else {
            bloodSugarDisplay.textContent = `今日血糖: ${data.bloodSugar} mg/dL`;
            if (deleteButton) deleteButton.style.display = 'block';
        }
    } catch (error) {
        console.error('Error:', error);
        bloodSugarDisplay.textContent = '載入失敗';
        if (deleteButton) deleteButton.style.display = 'none';
    }
}

async function deleteTodayBloodSugar() {
    const userIdInput = document.getElementById('userId');
    
    if (!userIdInput) {
        alert('無法找到用戶ID');
        return;
    }

    if (!confirm('確定要刪除今日的血糖記錄嗎？')) {
        return;
    }

    try {
        const response = await fetch(`/api/blood-sugar/today/${userIdInput.value}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            const data = await response.json();
            throw new Error(data.error || '刪除失敗');
        }

        alert('記錄已成功刪除');
        loadTodayBloodSugar();
    } catch (error) {
        console.error('Error:', error);
        alert(error.message || '刪除失敗');
    }
}