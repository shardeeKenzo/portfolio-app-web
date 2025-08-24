document.addEventListener('DOMContentLoaded', function () {
    const csrfTokenEl  = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderEl = document.querySelector('meta[name="_csrf_header"]');
    const csrfParamEl  = document.querySelector('meta[name="_csrf_parameter"]');

    const csrfToken  = csrfTokenEl  ? csrfTokenEl.content  : null;
    const csrfHeader = csrfHeaderEl ? csrfHeaderEl.content : null;

    const csrfParamName = csrfParamEl ? csrfParamEl.content : '_csrf';

    const removeButtons = document.querySelectorAll('.remove-stock-button');

    removeButtons.forEach(function (button) {
        button.addEventListener('click', function (e) {

            const stockId = this.getAttribute('data-stock-id');
            const accountId = window.location.pathname.split('/')[2];

            const form = document.createElement('form');
            form.method = 'POST';
            form.action = `/accounts/${accountId}/removestock`;

            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'stockId';
            input.value = stockId;
            form.appendChild(input);

            if (csrfToken) {
                const csrfInput = document.createElement('input');
                csrfInput.type = 'hidden';
                csrfInput.name = csrfParamName;
                csrfInput.value = csrfToken;
                form.appendChild(csrfInput);
            }

            document.body.appendChild(form);

            if (confirm(getMessage('app.removeStockConfirmation'))) {
                form.submit();
            } else {
                document.body.removeChild(form);
            }
        });
    });
});

function getMessage(key) {
    const el = document.getElementById(`msg-${key}`);
    return el ? el.getAttribute('data-message') : '';
}


