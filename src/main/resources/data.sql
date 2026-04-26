MERGE app_users AS target
    USING (VALUES ('admin', '$2a$10$0BtLUiUtoafN/1rcT7SrVuyfade5bA0NK4dlzMG0L8Eye9v.zwxkS', 'CREDIT_LIMIT_ADMIN'))
    AS source (username, password, role)
    ON target.username = source.username
    WHEN NOT MATCHED THEN
    INSERT (username, password, role)
    VALUES (source.username, source.password, source.role);


MERGE app_users AS target
USING (VALUES ('viewer', '$2a$10$lo4gC2SlY7x2D0zGErP.0eNrmjdsxddn1/E1tSOPd1e0XzA/t82Cy', 'CREDIT_LIMIT_VIEWER'))
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