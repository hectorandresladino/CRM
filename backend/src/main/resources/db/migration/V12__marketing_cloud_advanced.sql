-- V12: Marketing Cloud advanced - journeys, landing pages, attribution, social, A/B testing

-- Customer journeys
CREATE TABLE IF NOT EXISTS customer_journeys (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    trigger_type VARCHAR(50),
    trigger_criteria TEXT,
    segment_id BIGINT,
    status VARCHAR(20) DEFAULT 'DRAFT',
    total_enrolled INT DEFAULT 0,
    total_completed INT DEFAULT 0,
    total_converted INT DEFAULT 0,
    conversion_rate DOUBLE PRECISION DEFAULT 0.0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_journeys_tenant ON customer_journeys(tenant_id);

-- Journey steps
CREATE TABLE IF NOT EXISTS journey_steps (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    journey_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    delay_hours INT DEFAULT 0,
    action_config TEXT,
    condition_config TEXT,
    next_step_id BIGINT,
    branch_a_step_id BIGINT,
    branch_b_step_id BIGINT,
    order_num INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_journey_steps_tenant ON journey_steps(tenant_id);
CREATE INDEX IF NOT EXISTS idx_journey_steps_journey ON journey_steps(journey_id);

-- Landing pages
CREATE TABLE IF NOT EXISTS landing_pages (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    html_content TEXT,
    css_styles TEXT,
    meta_title VARCHAR(255),
    meta_description VARCHAR(500),
    form_config TEXT,
    thank_you_url VARCHAR(500),
    campaign_id BIGINT,
    total_visits INT DEFAULT 0,
    total_conversions INT DEFAULT 0,
    conversion_rate DOUBLE PRECISION DEFAULT 0.0,
    ab_test_variant VARCHAR(10),
    ab_test_parent_id BIGINT,
    is_published BOOLEAN DEFAULT FALSE,
    published_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_landing_tenant ON landing_pages(tenant_id);

-- Marketing attribution
CREATE TABLE IF NOT EXISTS marketing_attribution (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    contact_id BIGINT,
    client_id BIGINT,
    campaign_id BIGINT,
    touchpoint_type VARCHAR(50),
    touchpoint_channel VARCHAR(50),
    touchpoint_value DECIMAL(15,2),
    model VARCHAR(20) NOT NULL,
    attribution_weight DECIMAL(5,4),
    revenue_attributed DECIMAL(15,2),
    touchpoint_date TIMESTAMP,
    conversion_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_attribution_tenant ON marketing_attribution(tenant_id);
CREATE INDEX IF NOT EXISTS idx_attribution_campaign ON marketing_attribution(campaign_id);

-- Social media posts
CREATE TABLE IF NOT EXISTS social_media_posts (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    campaign_id BIGINT,
    platform VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    media_urls TEXT,
    hashtags TEXT,
    scheduled_at TIMESTAMP,
    published_at TIMESTAMP,
    external_post_id VARCHAR(255),
    external_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    likes_count INT DEFAULT 0,
    comments_count INT DEFAULT 0,
    shares_count INT DEFAULT 0,
    impressions INT DEFAULT 0,
    reach INT DEFAULT 0,
    clicks INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_social_tenant ON social_media_posts(tenant_id);
CREATE INDEX IF NOT EXISTS idx_social_status ON social_media_posts(status);
CREATE INDEX IF NOT EXISTS idx_social_scheduled ON social_media_posts(scheduled_at);

-- A/B tests
CREATE TABLE IF NOT EXISTS ab_tests (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(30) NOT NULL,
    variant_a_id BIGINT,
    variant_b_id BIGINT,
    variant_a_visits INT DEFAULT 0,
    variant_b_visits INT DEFAULT 0,
    variant_a_conversions INT DEFAULT 0,
    variant_b_conversions INT DEFAULT 0,
    variant_a_rate DOUBLE PRECISION DEFAULT 0.0,
    variant_b_rate DOUBLE PRECISION DEFAULT 0.0,
    confidence_level DOUBLE PRECISION DEFAULT 0.0,
    winning_variant VARCHAR(10),
    status VARCHAR(20) NOT NULL DEFAULT 'RUNNING',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_ab_tests_tenant ON ab_tests(tenant_id);
