-- USERS (AppUser → @Table(name = "application_user"))
-- Raw passwords for testing:
--   alice@example.com / alicepwd
--   bob@example.com   / bobpwd
--   admin@example.com / admin123
INSERT INTO application_user (username, password_hash, role)
VALUES
    ('alice@example.com', '$2b$10$Iaz3NZ4yGCI4Ht1ql.Xmh..cP/dY1g./ZXdfyx89mqXc774toSdq2', 'ROLE_USER'),
    ('bob@example.com',   '$2b$10$WNfc1bNPNj3pb8owrtd6Xe0cpw7FoyOfT.IpOtShAq/R.mfo2Hf1O', 'ROLE_USER'),
    ('admin@example.com', '$2b$10$g6TyeWDHAl9n8t9/0txZ/e5eSBm5sL0PMR8Yc71s9TjrSPouQbm7a', 'ROLE_ADMIN');

-- INVESTORS
INSERT INTO investors (name, contact_details, birth_date, risk_profile)
VALUES
    ('Alice Johnson',  'alice@example.com', '1985-04-12', 'CONSERVATIVE'),
    ('Bob Martinez',   'bob@example.com',   '1990-11-03', 'BALANCED'),
    ('Cara Nguyen',    'cara@example.com',  '1979-07-28', 'AGGRESSIVE');

-- STOCKS (now include created_by_user_id owner FK)
-- NOTE: sector values must match your Sector enum constants.
INSERT INTO stocks (symbol, company_name, current_price, sector, listed_date, image_url, created_by_user_id)
VALUES
    ('AAPL',  'Apple Inc.',             232.15, 'TECHNOLOGIES', '1980-12-12', 'https://example.com/aapl.png',
     (SELECT id FROM application_user WHERE username = 'alice@example.com')),
    ('MSFT',  'Microsoft Corporation',  415.22, 'TECHNOLOGIES', '1986-03-13', 'https://example.com/msft.png',
     (SELECT id FROM application_user WHERE username = 'alice@example.com')),
    ('JPM',   'JPMorgan Chase & Co.',   204.55, 'FINANCE',      '1969-01-01', 'https://example.com/jpm.png',
     (SELECT id FROM application_user WHERE username = 'admin@example.com')),
    ('XOM',   'Exxon Mobil Corporation',118.33, 'INDUSTRIAL',   '1920-01-01', 'https://example.com/xom.png',
     (SELECT id FROM application_user WHERE username = 'admin@example.com')),
    ('PFE',   'Pfizer Inc.',             36.10, 'HEALTHCARE',   '1942-01-01', 'https://example.com/pfe.png',
     (SELECT id FROM application_user WHERE username = 'bob@example.com'));

-- BROKERAGE ACCOUNTS
-- account_type must match your AccountType enum constants.
INSERT INTO brokerage_accounts (account_number, account_type, investor_id, balance, creation_date)
VALUES
    ('ACC-1001', 'INDIVIDUAL', (SELECT id FROM investors WHERE name = 'Alice Johnson'), 12500.00, '2023-01-05'),
    ('ACC-1002', 'INDIVIDUAL', (SELECT id FROM investors WHERE name = 'Alice Johnson'), 35000.00, '2023-06-18'),
    ('ACC-2001', 'JOINT',      (SELECT id FROM investors WHERE name = 'Bob Martinez'),   8200.50, '2022-09-09'),
    ('ACC-3001', 'RETIREMENT', (SELECT id FROM investors WHERE name = 'Cara Nguyen'),   98000.00, '2019-03-27');

-- LINK ACCOUNTS TO STOCKS (many-to-many)
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
