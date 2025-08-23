document.addEventListener('DOMContentLoaded', function () {
    // get CSRF (for dynamic forms)
    const csrfTokenEl  = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderEl = document.querySelector('meta[name="_csrf_header"]');
    const csrfParamEl  = document.querySelector('meta[name="_csrf_parameter"]'); // optional if present

    const csrfToken  = csrfTokenEl  ? csrfTokenEl.content  : null;
    const csrfHeader = csrfHeaderEl ? csrfHeaderEl.content : null;
    // Spring’s default request param name, if you ever need it explicitly:
    const csrfParamName = csrfParamEl ? csrfParamEl.content : '_csrf';

    // get all remove stock buttons
    const removeButtons = document.querySelectorAll('.remove-stock-button');

    removeButtons.forEach(function (button) {
        button.addEventListener('click', function (e) {
            // Let the regular form submit handle CSRF if user clicked the static form button
            // (We still support dynamic submission below when needed.)
            const stockId = this.getAttribute('data-stock-id');
            const accountId = window.location.pathname.split('/')[2]; // Assumes URL is /accounts/{id}

            // create a form dynamically to submit the removal (ensures it works even if no static form exists)
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = `/accounts/${accountId}/removestock`;

            // hidden input for stockId
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'stockId';
            input.value = stockId;
            form.appendChild(input);

            // CSRF hidden input (required for dynamically created forms)
            if (csrfToken) {
                const csrfInput = document.createElement('input');
                csrfInput.type = 'hidden';
                csrfInput.name = csrfParamName; // usually "_csrf"
                csrfInput.value = csrfToken;
                form.appendChild(csrfInput);
            }

            document.body.appendChild(form);

            // Confirm before submitting
            if (confirm(getMessage('app.removeStockConfirmation'))) {
                form.submit();
            } else {
                document.body.removeChild(form);
            }
        });
    });
});

/**
 * Helper to retrieve localized messages from data attributes.
 */
function getMessage(key) {
    const el = document.getElementById(`msg-${key}`);
    return el ? el.getAttribute('data-message') : '';
}


