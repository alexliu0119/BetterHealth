
const USDA_API_KEY = 'W1jAy9pJXg3F7uRKuJcvSQ0g1BzfOhhrPJyhHDYd';
const USDA_API_BASE_URL = 'https://api.nal.usda.gov/fdc/v1';


let userId;
let selectedFood = null;
let currentPage = 1;
const itemsPerPage = 7;
let allSearchResults = [];


window.resetDailyRecord = async function() {
    try {
        const userId = document.getElementById('userId').value;
        const response = await fetch(`/api/calorie-tracker/reset/${userId}`, {
            method: 'POST'
        });
        
        if (response.ok) {
            // 重置後立即更新顯示
            const userTargets = JSON.parse(localStorage.getItem('userTargets') || '{}');
            const data = {
                totalCalories: 0,
                waterIntake: 0,
                calorieTarget: userTargets.adjustedTdee || 0,
                waterTarget: userTargets.waterTarget || 0
            };
            
            updateProgressBars(data);
            await loadDailySummary(); 
            alert('每日記錄已重置');
        }
    } catch (error) {
        console.error('重置失敗:', error);
        alert('重置失敗');
    }
};
window.initializeCalorieTracker = async function() {
    try {
        const response = await fetch('/api/calorie-tracker/today');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        
      
        const userTargets = JSON.parse(localStorage.getItem('userTargets') || '{}');
        
   
        if (userTargets.adjustedTdee) {
            data.calorieTarget = userTargets.adjustedTdee;
        }


        const summaryData = {
            totalCalories: data.totalCalories || 0,
            calorieTarget: data.calorieTarget || userTargets.adjustedTdee || 0,
            waterIntake: data.waterIntake || 0,
            waterTarget: data.waterTarget || userTargets.waterTarget || 0
        };
    
        updateProgressBars(summaryData);
        
     
        const calorieTargetEl = document.getElementById('calorieTarget');
        if (calorieTargetEl) {
            calorieTargetEl.textContent = summaryData.calorieTarget;
        }
    } catch (error) {
        console.error('初始化卡路里追蹤器失敗:', error);
   
        const userTargets = JSON.parse(localStorage.getItem('userTargets') || '{}');
        const defaultData = {
            totalCalories: 0,
            calorieTarget: userTargets.adjustedTdee || 0,
            waterIntake: 0,
            waterTarget: userTargets.waterTarget || 0
        };
        updateProgressBars(defaultData);
    }
};


function updateProgressBars(data) {
    console.log('更新進度條數據:', data);
    
    const calorieProgress = document.getElementById('calorieProgress');
    const waterProgress = document.getElementById('waterProgress');
    
    if (calorieProgress) {
        const currentCalories = parseInt(data.totalCalories || 0);
        const targetCalories = parseInt(data.calorieTarget || 0);
        
        console.log('當前卡路里:', currentCalories);
        console.log('目標卡路里:', targetCalories);
        
        if (targetCalories > 0) {
            const percentage = (currentCalories / targetCalories) * 100;
            calorieProgress.style.width = `${Math.min(percentage, 100)}%`;
            calorieProgress.textContent = `${currentCalories}/${targetCalories} kcal`;
            
       
            const calorieTargetEl = document.getElementById('calorieTarget');
            if (calorieTargetEl) {
                calorieTargetEl.textContent = targetCalories;
            }
        }
    }
    
    if (waterProgress) {
        const currentWater = parseInt(data.waterIntake || 0);
        const waterTarget = parseInt(data.waterTarget || 0);
        
        if (waterTarget > 0) {
            const percentage = (currentWater / waterTarget) * 100;
            waterProgress.style.width = `${Math.min(percentage, 100)}%`;
            waterProgress.textContent = `${currentWater}/${waterTarget} ml`;
			console.log('當前飲水量:', currentWater);
			       console.log('飲水目標:', waterTarget);
       
            const waterTargetEl = document.getElementById('waterTarget');
            if (waterTargetEl) {
                waterTargetEl.textContent = waterTarget;
            }
        }
    }
}

function updateNutritionSummary(data) {
  
    const userTargets = JSON.parse(localStorage.getItem('userTargets') || '{}');
    const adjustedTdee = userTargets.adjustedTdee || data.calorieTarget;
    const calorieTargetEl = document.getElementById('calorieTarget');
    if (calorieTargetEl) {
        calorieTargetEl.textContent = adjustedTdee;  
    }

    const waterTargetEl = document.getElementById('waterTarget');
    if (waterTargetEl) {
        waterTargetEl.textContent = data.waterTarget || '0';
    }
    

    const currentCaloriesEl = document.getElementById('currentCalories');
    if (currentCaloriesEl) {
        currentCaloriesEl.textContent = data.totalCalories || '0';
    }

    const currentWaterEl = document.getElementById('currentWater');
    if (currentWaterEl) {
        currentWaterEl.textContent = data.waterIntake || '0';
    }
}

async function loadDailySummary() {
    try {
        const userId = document.getElementById('userId').value;
        console.log('正在加載用戶ID:', userId, '的每日摘要');
        
        const response = await axios.get(`/api/daily-record/daily-summary/${userId}`);
        const data = response.data;
        console.log('API返回的數據:', data);
		const waterTarget = data.waterTarget || (data.weight ? Math.round(data.weight * 33) : 2000);
		        
        
		updateProgressBars({
		          totalCalories: data.totalCalories,
		          calorieTarget: data.calorieTarget,  
		          waterIntake: data.waterIntake,
		         waterTarget: waterTarget
		      });
			  const waterTargetEl = document.getElementById('waterTarget');
			         if (waterTargetEl) {
			             waterTargetEl.textContent = waterTarget;
			         }
    } catch (error) {
        console.error('加載每日摘要失敗:', error);
    }
}

async function recordWater() {
    try {
        const userId = document.getElementById('userId').value;
        const amount = document.getElementById('waterAmount').value;

        if (!userId) {
            alert('未找到用戶ID');
            return;
        }
		

        if (!amount || amount <= 0) {
            alert('請輸入有效的飲水量');
            return;
        }

        const response = await axios.post('/api/daily-record/water', {
            userId: userId,
            currentAmount: (amount)
      
        }).then((response) => {
			loadDailySummary();
            alert('飲水記錄成功！');
            document.getElementById('waterAmount').value = '';
		})
    } catch (error) {
        console.error('記錄飲水失敗:', error);
		console.log('錯誤詳情' + error.response.data);
        if (error.response) {
            alert('記錄失敗: ' + error.response.data);
        } else {
            alert('記錄失敗: ' + error.message);
        }
    }
}


async function searchFood(query) {
    try {
       
        const loadingIndicator = document.getElementById('loadingIndicator');
        if (loadingIndicator) loadingIndicator.style.display = 'block';

        const response = await axios.get(`/api/food/search?query=${encodeURIComponent(query)}`);
        console.log('搜尋結果:', response.data);
        
        if (response.data && Array.isArray(response.data)) {
            displaySearchResults(response.data);
        } else {
            console.error('搜尋結果格式不正確:', response.data);
            alert('搜尋結果格式不正確');
        }
    } catch (error) {
        console.error('搜尋食物失敗:', error);
        alert('搜尋食物失敗: ' + error.message);
    } finally {
       
        const loadingIndicator = document.getElementById('loadingIndicator');
        if (loadingIndicator) loadingIndicator.style.display = 'none';
    }
}


function displaySearchResults(foods) {
    if (!Array.isArray(foods) || foods.length === 0) {
        const resultsList = document.getElementById('searchResults');
        resultsList.innerHTML = '<div class="alert alert-info">沒有找到相關食物</div>';
        return;
    }

    allSearchResults = foods;
    const resultsList = document.getElementById('searchResults');
    resultsList.innerHTML = '';

    const totalPages = Math.ceil(foods.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = Math.min(startIndex + itemsPerPage, foods.length);

	for(let i = startIndex; i < endIndex; i++) {
	        const food = foods[i];
	        const li = document.createElement('li');
	        li.className = 'list-group-item';

	        const nutrients = {
	            calories: food.foodNutrients[3]?.value || 0,
	            protein: food.foodNutrients[0]?.value || 0,
	            fat: food.foodNutrients[1]?.value || 0,
	            carbs: food.foodNutrients[2]?.value || 0
	        };


        const description = food.description.replace(/'/g, "\\'");
        
		li.innerHTML = `
		         <div class="w-100">
		             <strong>${food.description}</strong>
		             <div class="row mt-2">
		                 <div class="col-md-6">
		                     <div class="base-nutrition">
		                         每100g營養值：<br>
		                         熱量: ${nutrients.calories}kcal<br>
		                         蛋白質: ${nutrients.protein}g<br>
		                         脂肪: ${nutrients.fat}g<br>
		                         碳水化合物: ${nutrients.carbs}g
		                     </div>
		                 </div>
		                 <div class="col-md-6">
		                     <div class="actual-nutrition" style="color: #0d6efd; font-weight: bold;">
		                         <!-- 這裡會由 updatePortionNutrition 更新 -->
		                     </div>
		                 </div>
		             </div>
		             <div class="mt-2">
		                 <input type="number" class="form-control form-control-sm d-inline-block w-25" 
		                        value="100" min="1" step="1" 
		                        onchange="updatePortionNutrition(this, ${i})"
		                        oninput="updatePortionNutrition(this, ${i})">
		                 <span class="ms-2">克</span>
		                 <button class="btn btn-primary btn-sm ms-2" 
		                         onclick="addFood('${description}', 
		                                      ${nutrients.calories}, 
		                                      ${nutrients.protein}, 
		                                      ${nutrients.carbs}, 
		                                      ${nutrients.fat})">
		                     添加
		                 </button>
		             </div>
		         </div>
		     `;
		     resultsList.appendChild(li);
		  
		     const input = li.querySelector('input');
		     updatePortionNutrition(input, i);
		 }

    if (totalPages > 1) {
        const pagination = document.createElement('div');
        pagination.className = 'pagination justify-content-center mt-3';
        pagination.innerHTML = `
            <button class="btn btn-outline-primary me-2" 
                    ${currentPage === 1 ? 'disabled' : ''} 
                    onclick="changePage(${currentPage - 1})">
                上一頁
            </button>
            <span class="mx-2">第 ${currentPage} 頁，共 ${totalPages} 頁</span>
            <button class="btn btn-outline-primary ms-2" 
                    ${currentPage === totalPages ? 'disabled' : ''} 
                    onclick="changePage(${currentPage + 1})">
                下一頁
            </button>
        `;
        resultsList.appendChild(pagination);
    }
}


function updatePortionNutrition(input, foodIndex) {
    const food = allSearchResults[foodIndex];
    const portion = parseFloat(input.value);
    const multiplier = portion / 100;
    
    const parentLi = input.closest('li');
    const actualNutritionDiv = parentLi.querySelector('.actual-nutrition');
    
    const nutrients = {
        calories: food.foodNutrients[3]?.value || 0,
        protein: food.foodNutrients[0]?.value || 0,
        fat: food.foodNutrients[1]?.value || 0,
        carbs: food.foodNutrients[2]?.value || 0
    };

    actualNutritionDiv.innerHTML = `
        <div class="text-primary">
            ${portion}g 實際營養值：<br>
            熱量: ${(nutrients.calories * multiplier).toFixed(1)}kcal<br>
            蛋白質: ${(nutrients.protein * multiplier).toFixed(1)}g<br>
            脂肪: ${(nutrients.fat * multiplier).toFixed(1)}g<br>
            碳水化合物: ${(nutrients.carbs * multiplier).toFixed(1)}g
        </div>
    `;
}


function addFood(name, calories, protein, carbs, fat) {
    console.log('Adding food:', { name, calories, protein, carbs, fat });
    
    const portionInput = document.querySelector(`button[onclick*="${name}"]`)
                               .parentElement.querySelector('input');
    const portion = parseFloat(portionInput.value);
    const multiplier = portion / 100;

    selectedFood = {
        name: name,
        calories: parseFloat(calories * multiplier) || 0,
        protein: parseFloat(protein * multiplier) || 0,
        carbs: parseFloat(carbs * multiplier) || 0,
        fat: parseFloat(fat * multiplier) || 0
    };

   
    const modalBody = document.getElementById('selectedFoodModalBody');
    modalBody.innerHTML = `
        <div class="selected-food-info">
            <p>食物名稱: ${name}</p>
            <p>份量: ${portion}g</p>
            <p>熱量: ${selectedFood.calories.toFixed(1)}kcal</p>
            <p>蛋白質: ${selectedFood.protein.toFixed(1)}g</p>
            <p>碳水化合物: ${selectedFood.carbs.toFixed(1)}g</p>
            <p>脂肪: ${selectedFood.fat.toFixed(1)}g</p>
        </div>
    `;

    
    const modal = new bootstrap.Modal(document.getElementById('selectedFoodModal'));
    modal.show();
}


function changePage(newPage) {
    currentPage = newPage;
    displaySearchResults(allSearchResults);
}

async function recordFoodAndCloseModal() {
    await recordFood();
    const modal = bootstrap.Modal.getInstance(document.getElementById('selectedFoodModal'));
    modal.hide();
}


async function recordFood() {
    if (!selectedFood) return;

    try {
        const userId = document.getElementById('userId').value;
        if (!userId) {
            alert('未找到用戶ID');
            return;
        }

        const response = await axios.post('/api/food/record', {
            userId: parseInt(userId),
            foodName: selectedFood.name,
            calories: selectedFood.calories,
            protein: selectedFood.protein,
            carbs: selectedFood.carbs,
            fat: selectedFood.fat
        });

        if (response.status === 200) {
            await loadDailySummary();
            alert('食物記錄成功');
            
           
            document.getElementById('searchResults').innerHTML = '';
            document.getElementById('foodSearch').value = '';
        }
    } catch (error) {
        console.error('記錄食物失敗:', error);
        console.log('錯誤詳情:', error.response?.data);
        alert('記錄失敗: ' + (error.response?.data?.error || error.message));
    }
}


document.addEventListener('DOMContentLoaded', function() {
    const userId = document.getElementById('userId').value;
    if (!userId) {
        console.error('未找到用戶ID');
        return;
    }
    
    loadDailySummary();
    
    
    const searchInput = document.getElementById('foodSearch');
    if (searchInput) {
        searchInput.addEventListener('input', debounce((e) => {
            if (e.target.value.length >= 2) {
                searchFood(e.target.value);
            }
        }, 500));
    }
});


function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}