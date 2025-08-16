-- INVESTORS
INSERT INTO investors (name, contact_details, birth_date, risk_profile)
VALUES
    ('Alice Johnson',  'alice@example.com', '1985-04-12', 'CONSERVATIVE'),
    ('Bob Martinez',   'bob@example.com',   '1990-11-03', 'BALANCED'),
    ('Cara Nguyen',    'cara@example.com',  '1979-07-28', 'AGGRESSIVE');

-- STOCKS
-- sector must match your Sector enum constants (e.g., TECHNOLOGIES, HEALTHCARE, FINANCE, ENERGY, CONSUMER)
INSERT INTO stocks (symbol, company_name, current_price, sector, listed_date, image_url)
VALUES
    ('AAPL',  'Apple Inc.',             232.15, 'TECHNOLOGIES',        '1980-12-12', 'https://example.com/aapl.png'),
    ('MSFT',  'Microsoft Corporation',  415.22, 'TECHNOLOGIES',        '1986-03-13', 'https://example.com/msft.png'),
    ('JPM',   'JPMorgan Chase & Co.',    204.55, 'FINANCE',     '1969-01-01', 'https://example.com/jpm.png'),
    ('XOM',   'Exxon Mobil Corporation', 118.33, 'INDUSTRIAL',      '1920-01-01', 'https://example.com/xom.png'),
    ('PFE',   'Pfizer Inc.',              36.10, 'HEALTHCARE',  '1942-01-01', 'https://example.com/pfe.png');

-- BROKERAGE ACCOUNTS
-- account_type must match your AccountType enum constants (e.g., CASH, MARGIN, RETIREMENT, BROKERAGE)
INSERT INTO brokerage_accounts (account_number, account_type, investor_id, balance, creation_date)
VALUES
    ('ACC-1001', 'INDIVIDUAL',       (SELECT id FROM investors WHERE name = 'Alice Johnson'),  12500.00, '2023-01-05'),
    ('ACC-1002', 'INDIVIDUAL',     (SELECT id FROM investors WHERE name = 'Alice Johnson'),  35000.00, '2023-06-18'),
    ('ACC-2001', 'JOINT',  (SELECT id FROM investors WHERE name = 'Bob Martinez'),    8200.50, '2022-09-09'),
    ('ACC-3001', 'RETIREMENT', (SELECT id FROM investors WHERE name = 'Cara Nguyen'),    98000.00, '2019-03-27');

-- LINK ACCOUNTS TO STOCKS (many-to-many)
-- We reference accounts by account_number and stocks by symbol so we don't care what auto IDs were generated.
INSERT INTO account_stocks (account_id, stock_id)
VALUES
    ((SELECT id FROM brokerage_accounts WHERE account_number = 'ACC-1001'),
     (SELECT id FROM stocks WHERE symbol = 'AAPL')),
    ((SELECT id FROM brokerage_accounts WHERE account_number = 'ACC-1001'),
     (SELECT id FROM stocks WHERE symbol = 'MSFT')),
    ((SELECT id FROM brokerage_accounts WHERE account_number = 'ACC-1002'),
     (SELECT id FROM stocks WHERE symbol = 'AAPL')),
    ((SELECT id FROM brokerage_accounts WHERE account_number = 'ACC-2001'),
     (SELECT id FROM stocks WHERE symbol = 'PFE')),
    ((SELECT id FROM brokerage_accounts WHERE account_number = 'ACC-3001'),
     (SELECT id FROM stocks WHERE symbol = 'JPM')),
    ((SELECT id FROM brokerage_accounts WHERE account_number = 'ACC-3001'),
     (SELECT id FROM stocks WHERE symbol = 'XOM'));