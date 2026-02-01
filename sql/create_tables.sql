CREATE TABLE IF NOT EXISTS "user"
(
    id BIGSERIAL PRIMARY KEY,
    tg_id BIGINT NOT NULL UNIQUE,
    firstname TEXT,
    lastname TEXT,
    registered TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS budget
(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    creator_id BIGINT NOT NULL REFERENCES "user"(id),
    currency TEXT NOT NULL DEFAULT 'RUB',
    created TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS category
(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    type VARCHAR(6) NOT NULL,
    UNIQUE (user_id, name)
);

CREATE TABLE IF NOT EXISTS category_keyword
(
    id BIGSERIAL PRIMARY KEY,
    keyword TEXT NOT NULL,
    category_id BIGINT NOT NULL REFERENCES category(id),
    UNIQUE (keyword, category_id)
);

CREATE TABLE IF NOT EXISTS operation
(
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT REFERENCES category(id),
    budget_id BIGINT REFERENCES budget(id),
    change FLOAT NOT NULL,
    keyword VARCHAR(255),
    created TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    edited TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    type VARCHAR(6)
);

CREATE INDEX IF NOT EXISTS operation_type_idx ON operation(type);
CREATE INDEX IF NOT EXISTS operation_keyword_idx ON operation(keyword);
