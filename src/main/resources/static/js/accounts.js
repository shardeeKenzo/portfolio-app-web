

document.addEventListener('DOMContentLoaded', function () {
    // get all remove stock buttons
    const removeButtons = document.querySelectorAll('.remove-stock-button');

    removeButtons.forEach(function (button) {
        button.addEventListener('click', function () {
            const stockId = this.getAttribute('data-stock-id');
            const accountId = window.location.pathname.split('/')[2]; // Assumes URL is /accounts/{id}

            // create a form dynamically to submit the removal
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = `/accounts/${accountId}/removestock`;

            // create hidden input for stockId
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'stockId';
            input.value = stockId;
            form.appendChild(input);

            // append form to body
            document.body.appendChild(form);

            // Confirm before submitting
            if (confirm(getMessage('app.removeStockConfirmation'))) {
                form.submit();
            } else {
                // remove the form if user cancels
                document.body.removeChild(form);
            }
        });
    });
});

/**
 * Helper function to retrieve localized messages from data attributes or other sources.
 * This implementation assumes that you have a mechanism to inject localized messages into the DOM,
 * such as using a hidden element with data attributes.
 */
function getMessage(key) {
    const el = document.getElementById(`msg-${key}`);
    return el ? el.getAttribute('data-message') : '';
}

