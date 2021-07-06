Date.prototype.withoutTime = function () {
    const date = new Date(this);
    date.setHours(0, 0, 0, 0);
    return date;
}

const form = document.getElementById('orderForm');
const startDate = document.getElementById('startDate');
const endDate = document.getElementById('endDate');
const warning1 = document.getElementById('warning1');
const warning3 = document.getElementById('warning3');

startDate.addEventListener('input', () => {
    const now = new Date().withoutTime();
    const start = Date.parse(startDate.value);
    warning1.hidden = start >= now;
});

endDate.addEventListener('input', () => {
    const now = new Date().withoutTime();
    const end = Date.parse(endDate.value);
    warning1.hidden = end >= now;
});

form.addEventListener('submit', (event) => {
    const now = new Date().withoutTime();
    const start = Date.parse(startDate.value);
    const end = Date.parse(endDate.value);
    if (start < now || end < now || start > end) {
        warning3.hidden = false;
        event.preventDefault();
        return false;
    }
    warning3.hidden = true;
    return true;
});
