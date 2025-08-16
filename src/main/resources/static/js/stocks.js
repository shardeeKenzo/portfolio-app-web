// stocks.js (no bundler, global namespace)
(function () {
    const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]')?.content;
    const csrfToken      = document.querySelector('meta[name="_csrf"]')?.content;

    function jsonHeaders(includeBody = false) {
        const base = { Accept: "application/json" }; // Required by your REST spec
        if (includeBody) base["Content-Type"] = "application/json";
        if (csrfHeaderName && csrfToken) base[csrfHeaderName] = csrfToken; // CSRF for DELETE
        return base;
    }

    async function fetchStocksForAccount(accountId) {
        const resp = await fetch(`/api/accounts/${accountId}/stocks`, { headers: jsonHeaders() });
        if (resp.status === 200) return resp.json();
        if (resp.status === 204) return [];
        throw new Error(`Failed to fetch stocks for account ${accountId}: ${resp.status}`);
    }

    async function searchStocks({ symbol, minPrice, maxPrice } = {}) {
        const qs = new URLSearchParams();
        if (symbol) qs.set("symbol", symbol);
        if (minPrice) qs.set("minPrice", minPrice);
        if (maxPrice) qs.set("maxPrice", maxPrice);
        const url = `/api/stocks${qs.toString() ? `?${qs}` : ""}`;
        const resp = await fetch(url, { headers: jsonHeaders() });
        if (resp.status === 200) return resp.json();
        if (resp.status === 204) return [];
        throw new Error(`Search failed: ${resp.status}`);
    }

    async function deleteStock(stockId) {
        const resp = await fetch(`/api/stocks/${stockId}`, {
            method: "DELETE",
            headers: jsonHeaders()
        });
        if (resp.status === 204) return true;  // success
        if (resp.status === 404) return false; // not found
        throw new Error(`Delete failed: ${resp.status}`);
    }

    // Simple DOM hookup: any button with .btn-delete and data-id
    document.addEventListener("click", async (e) => {
        const btn = e.target.closest(".btn-delete");
        if (!btn) return;
        e.preventDefault(); // avoid form submissions
        const id = btn.dataset.id;
        const ok = await deleteStock(id);
        if (ok) document.querySelector(`#row-${id}`)?.remove();
    });

    // Expose helpers if you want to call them manually from the console
    window.stocksApi = { fetchStocksForAccount, searchStocks, deleteStock };
})();