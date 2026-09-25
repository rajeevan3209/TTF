-- Schemes
INSERT INTO scheme (code, name, qr_identifier_pattern, message_format) VALUES
  ('PAYNOW_QR', 'PayNow QR', 'PAYNOW_QR|<merchant-proxy-id>', 'PAYNOW_JSON_V1'),
  ('NETS_QR', 'NETS QR', 'NETS_QR|<merchant-terminal-id>', 'NETS_ISO8583_LITE');

-- Participants (mix of PayNow QR and NETS QR, acquirer/issuer roles)
INSERT INTO participant (id, name, scheme_code, participant_type, status) VALUES
  (1, 'DBS PayNow Merchant Acquiring', 'PAYNOW_QR', 'ACQUIRER', 'ACTIVE'),
  (2, 'DBS PayNow Consumer App', 'PAYNOW_QR', 'ISSUER', 'ACTIVE'),
  (3, 'OCBC PayNow Consumer App', 'PAYNOW_QR', 'ISSUER', 'ACTIVE'),
  (4, 'NETS Merchant Acquiring', 'NETS_QR', 'ACQUIRER', 'ACTIVE'),
  (5, 'NETS Debit Consumer App', 'NETS_QR', 'ISSUER', 'ACTIVE'),
  (6, 'Suspended Test Bank', 'NETS_QR', 'ISSUER', 'SUSPENDED');

ALTER TABLE participant ALTER COLUMN id RESTART WITH 7;

-- Fee configuration for off-us transactions (0.3% - 0.7% band, capped at SGD 100 per transaction)
INSERT INTO fee_config (id, tier, fee_percentage, fee_cap) VALUES
  (1, 'STANDARD', 0.005, 100.00);

ALTER TABLE fee_config ALTER COLUMN id RESTART WITH 2;
