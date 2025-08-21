// stocks.js (extends your existing file)
(function () {
    const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]')?.content;
    const csrfToken      = document.querySelector('meta[name="_csrf"]')?.content;

    function jsonHeaders(withBody = false) {
        const h = { Accept: "application/json" };
        if (withBody) h["Content-Type"] = "application/json";
        if (csrfHeaderName && csrfToken) h[csrfHeaderName] = csrfToken; // CSRF for POST/PATCH/DELETE
        return h;
    }

    async function createStock(newStock) {
        const resp = await fetch(`/api/stocks`, {
            method: "POST",
            headers: jsonHeaders(true),
            body: JSON.stringify(newStock)
        });
        if (resp.status === 201) return resp.json();
        if (resp.status === 400) throw new Error("Validation failed (400).");
        throw new Error(`Create failed: ${resp.status}`);
    }

    async function patchStock(id, partial) {
        const resp = await fetch(`/api/stocks/${id}`, {
            method: "PATCH",
            headers: jsonHeaders(true),
            body: JSON.stringify(partial)
        });
        if (resp.status === 200) return resp.json();
        if (resp.status === 404) throw new Error("Not found.");
        if (resp.status === 400) throw new Error("Validation failed (400).");
        throw new Error(`Patch failed: ${resp.status}`);
    }

    // Minimal Bootstrap-friendly DOM glue (example)
    function renderCard(stock) {
        const img = stock.imageURL && stock.imageURL.trim() ? stock.imageURL : "/img/stock-placeholder.svg";
        return `
      <div class="col-md-4" id="row-${stock.id}">
        <div class="card">
            <img src="${img}" class="card-img-top stock-img" alt="Stock Image">
          <div class="card-body">
            <h5 class="card-title">${stock.companyName}</h5>
            <p>
              <strong>Symbol:</strong> ${stock.symbol}<br>
              <strong>Current Price:</strong> $${stock.currentPrice}<br>
              <strong>Sector:</strong> ${stock.sector}<br>
              <strong>Listed Date:</strong> ${stock.listedDate || ''}
            </p>
            <button class="btn btn-secondary btn-sm btn-quick-edit" data-id="${stock.id}">Quick Edit</button>
            <button class="btn btn-danger btn-sm btn-delete" data-id="${stock.id}">Delete</button>
          </div>
        </div>
      </div>`;
    }

    // Example: quick “add” handler (bind to a Bootstrap modal or a small form)
    document.addEventListener("submit", async (e) => {
        if (e.target.matches("#api-add-stock-form")) {
            e.preventDefault();
            const form = e.target;
            try {
                const created = await createStock({
                    symbol: form.symbol.value,
                    companyName: form.companyName.value,
                    currentPrice: Number(form.currentPrice.value || 0),
                    sector: form.sector.value,             // enum string
                    listedDate: form.listedDate.value || null,
                    imageURL: form.imageURL.value || null
                });
                document.querySelector(".row").insertAdjacentHTML("afterbegin", renderCard(created));
                form.reset();
            } catch (err) { alert(err.message); }
        }
    });

    // Example: quick inline PATCH to change price
    document.addEventListener("click", async (e) => {
        const btn = e.target.closest(".btn-quick-edit");
        if (!btn) return;
        const id = btn.dataset.id;
        const newPrice = prompt("New price?");
        if (!newPrice) return;
        try {
            const updated = await patchStock(id, { currentPrice: Number(newPrice) });
            const card = document.querySelector(`#row-${id} .card-body`);
            if (card) card.querySelector("p").innerHTML =
                card.querySelector("p").innerHTML.replace(/Current Price:.+\$/i, `Current Price:</strong> $${updated.currentPrice}`);
        } catch (err) { alert(err.message); }
    });

    // expose for debugging
    window.stocksApi = { createStock, patchStock };
})();