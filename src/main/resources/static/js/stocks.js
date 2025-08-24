// stocks.js
// Works with Spring Security enabled: sends CSRF for writes and handles 403s.
// Requires in <head> (per slides):
//   <meta name="_csrf" th:content="${_csrf.token}">
//   <meta name="_csrf_header" th:content="${_csrf.headerName}">

(function () {
    const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]')?.content;
    const csrfToken      = document.querySelector('meta[name="_csrf"]')?.content;

    function isWrite(method) {
        const m = (method || 'GET').toUpperCase();
        return m !== 'GET' && m !== 'HEAD' && m !== 'OPTIONS';
    }

    function jsonHeaders(hasBody = false) {
        const h = { Accept: 'application/json' };
        if (hasBody) h['Content-Type'] = 'application/json';
        if (csrfHeaderName && csrfToken) h[csrfHeaderName] = csrfToken;
        return h;
    }

    async function apiFetch(url, { method = 'GET', headers = {}, body } = {}) {
        const opts = {
            method,
            credentials: 'same-origin', // send session cookie
            headers: { ...jsonHeaders(!!body), ...headers }
        };
        if (body) opts.body = typeof body === 'string' ? body : JSON.stringify(body);

        const resp = await fetch(url, opts);

        if (resp.status === 204) return null;
        if (resp.ok) {
            // try to parse JSON only when present
            const ct = resp.headers.get('Content-Type') || '';
            if (ct.includes('application/json')) return resp.json();
            return null;
        }

        if (resp.status === 403) {
            showAlert('You are not allowed to perform this action. Please sign in or check your permissions.', 'warning');

            if (confirm('This action requires you to be signed in. Go to the login page now?')) {
                const back = encodeURIComponent(location.pathname + location.search + location.hash);
                location.href = `/login?from=${back}`;
            }
            throw new Error('Forbidden (403)');
        }

        if (resp.status === 400) throw new Error('Bad request (400): Validation failed.');
        if (resp.status === 404) throw new Error('Not found (404).');
        if (resp.status === 409) throw new Error('Conflict (409).');

        throw new Error(`Request failed: HTTP ${resp.status}`);
    }

    async function createStock(newStock) {
        return apiFetch('/api/stocks', {
            method: 'POST',
            body: newStock
        });
    }

    async function patchStock(id, partial) {
        return apiFetch(`/api/stocks/${id}`, {
            method: 'PATCH',
            body: partial
        });
    }

    async function deleteStock(id) {
        await apiFetch(`/api/stocks/${id}`, { method: 'DELETE' });
    }

    function renderCard(stock) {
        const img = stock.imageURL && stock.imageURL.trim() ? stock.imageURL : '/img/stock-placeholder.svg';
        const listed = stock.listedDate || '';
        const createdBy = stock.createdByEmail ? `Created by: ${stock.createdByEmail}` : '';

        return `
      <div class="col-md-4" id="row-${stock.id}">
        <div class="card h-100">
          <img src="${img}" class="card-img-top stock-img" alt="Stock Image">
          <div class="card-body d-flex flex-column">
            <h5 class="card-title">${stock.companyName}</h5>
            <p class="mb-2">
              <strong>Symbol:</strong> ${stock.symbol}<br>
              <strong>Current Price:</strong> $<span class="js-price">${stock.currentPrice}</span><br>
              <strong>Sector:</strong> ${stock.sector}<br>
              <strong>Listed Date:</strong> ${listed}<br>
              ${createdBy ? `<small class="text-muted">${createdBy}</small>` : ``}
            </p>
            <div class="mt-auto">
              <button class="btn btn-secondary btn-sm me-2 btn-quick-edit" data-id="${stock.id}">Quick Edit</button>
              <button class="btn btn-danger btn-sm btn-delete" data-id="${stock.id}">Delete</button>
            </div>
          </div>
        </div>
      </div>`;
    }

    function showAlert(message, type = 'info') {
        const box = document.createElement('div');
        box.className = `alert alert-${type} position-fixed top-0 start-50 translate-middle-x mt-3 shadow`;
        box.role = 'alert';
        box.style.zIndex = 2000;
        box.textContent = message;
        document.body.appendChild(box);
        setTimeout(() => box.remove(), 3000);
    }

    document.addEventListener('submit', async (e) => {
        if (!e.target.matches('#api-add-stock-form')) return;
        e.preventDefault();
        const form = e.target;
        try {
            const created = await createStock({
                symbol: form.symbol.value,
                companyName: form.companyName.value,
                currentPrice: Number(form.currentPrice.value || 0),
                sector: form.sector.value,             // enum string
                listedDate: form.listedDate.value || null,
                imageURL: form.imageURL?.value || null
            });
            const row = document.querySelector('.row');
            if (row) row.insertAdjacentHTML('afterbegin', renderCard(created));
            form.reset();
            showAlert('Stock created.', 'success');
        } catch (err) {
            showAlert(err.message, 'danger');
        }
    });

    document.addEventListener('click', async (e) => {
        const btn = e.target.closest('.btn-quick-edit');
        if (!btn) return;
        const id = btn.dataset.id;
        const newPriceStr = prompt('New price?');
        if (!newPriceStr) return;
        const newPrice = Number(newPriceStr);
        if (Number.isNaN(newPrice)) return showAlert('Please enter a valid number.', 'warning');

        try {
            const updated = await patchStock(id, { currentPrice: newPrice });
            const card = document.querySelector(`#row-${id}`);
            card?.querySelector('.js-price')?.replaceChildren(document.createTextNode(updated.currentPrice));
            showAlert('Price updated.', 'success');
        } catch (err) {
            showAlert(err.message, 'danger');
        }
    });

    document.addEventListener('click', async (e) => {
        const btn = e.target.closest('.btn-delete');
        if (!btn) return;
        const id = btn.dataset.id;
        if (!confirm('Delete this stock?')) return;
        try {
            await deleteStock(id);
            document.getElementById(`row-${id}`)?.remove();
            showAlert('Stock deleted.', 'success');
        } catch (err) {
            showAlert(err.message, 'danger');
        }
    });

    window.stocksApi = { createStock, patchStock, deleteStock };
})();
