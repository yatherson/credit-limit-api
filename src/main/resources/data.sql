MERGE app_users AS target
    USING (VALUES ('admin', '$2a$10$ixlPY3AAd4ty1l6E2IsQ9OFZi2ba9ZQE0bP7RFcGIWNhyFrrT3YUi', 'CREDIT_LIMIT_ADMIN'))
    AS source (username, password, role)
    ON target.username = source.username
    WHEN NOT MATCHED THEN
    INSERT (username, password, role)
    VALUES (source.username, source.password, source.role);
GO

MERGE app_users AS target
USING (VALUES ('viewer', '$2a$10$Ek9YFyKG3HLMJb4GS3l5eutg0DPMxJgFJPCH7YFVsT3a0iR5NbKuS', 'CREDIT_LIMIT_VIEWER'))
AS source (username, password, role)
ON target.username = source.username
WHEN NOT MATCHED THEN
    INSERT (username, password, role)
    VALUES (source.username, source.password, source.role);
GO

MERGE customers AS target
USING (VALUES ('John Doe', 0, 5000.00))
AS source (name, is_vip, credit_limit)
ON target.name = source.name
WHEN NOT MATCHED THEN
    INSERT (name, is_vip, credit_limit)
    VALUES (source.name, source.is_vip, source.credit_limit);
GO

MERGE customers AS target
USING (VALUES ('Jane VIP', 1, 2000.00))
AS source (name, is_vip, credit_limit)
ON target.name = source.name
WHEN NOT MATCHED THEN
    INSERT (name, is_vip, credit_limit)
    VALUES (source.name, source.is_vip, source.credit_limit);
GO