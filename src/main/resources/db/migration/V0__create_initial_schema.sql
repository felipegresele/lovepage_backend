-- Tabela de usuários
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Tabela de páginas de amor
CREATE TABLE IF NOT EXISTS love_pages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    slug VARCHAR(255) UNIQUE,
    receiver_name VARCHAR(255),
    sender_name VARCHAR(255),
    message TEXT,
    relationship_start_date DATE,
    music_id VARCHAR(255),
    music_title VARCHAR(255),
    theme VARCHAR(255),
    plan_type VARCHAR(50),
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP,
    payment_id VARCHAR(255),
    qr_code_frame VARCHAR(50),
    retrospectiva JSONB
);

-- Tabela de fotos das páginas
CREATE TABLE IF NOT EXISTS page_photos (
    page_id UUID REFERENCES love_pages(id),
    photo_url VARCHAR(255)
);

-- Tabela de pagamentos
CREATE TABLE IF NOT EXISTS payment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    amount NUMERIC(19, 2),
    status VARCHAR(255),
    provider VARCHAR(255),
    page_id UUID REFERENCES love_pages(id),
    created_at TIMESTAMP
);
