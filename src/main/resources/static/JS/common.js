
const API_BASE_URL = '/api';


async function apiRequest(endpoint, options = {}) {
    const response = await fetch(API_BASE_URL + endpoint, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...options.headers
        }
    });
    
    if (!response.ok) {
        throw new Error(`API request failed: ${response.statusText}`);
    }
    
    return response.json();
}


function handleError(error) {
    console.error('Error:', error);
    let message = '操作失敗';
    if(error.response) {
        message = error.response.data?.message || message;
    }
    alert(message);
}


function formatDate(date) {
    return new Date(date).toLocaleDateString('zh-TW');
}


function formatNumber(number, decimals = 1) {
    return Number(number).toFixed(decimals);
}
