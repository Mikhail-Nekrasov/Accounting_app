function saveEntryToSession(formSelector) {
    const form = document.querySelector(formSelector);
    if (!form) return;

    const formData = new FormData(form);

    fetch('/saveEntrySession', {
        method: 'POST',
        body: formData
    }).catch(err => console.error('Failed to save entry to session', err));
}

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.add-category-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            saveEntryToSession('form'); 
        });
    });
});
