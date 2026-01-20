-- 유저
CREATE TABLE "users" (
                         "id"				uuid			NOT NULL,
                         "email"				varchar(30)		NOT NULL,
                         "nickname"			varchar(30)		NOT NULL,
                         "password"			varchar(30)		NOT NULL,
                         "created_at"		timestamp		DEFAULT NOW() NOT NULL,
                         "updated_at"		timestamp		NULL,
                         "deleted_at"		timestamp		NULL
);
ALTER TABLE "users" ADD CONSTRAINT "PK_USERS" PRIMARY KEY ("id");
ALTER TABLE "users" ADD CONSTRAINT uk_users_email UNIQUE ("email");
ALTER TABLE "users" ADD CONSTRAINT uk_users_nickname UNIQUE ("nickname");

-- 관심사
CREATE TABLE "interests" (
                             "id"				uuid			NOT NULL,
                             "name"				varchar(50)		NOT NULL,
                             "subscriber_count"	BIGINT			DEFAULT 0	NOT NULL,
                             "created_at"		timestamp		DEFAULT NOW() NOT NULL,
                             "updated_at"		timestamp		NULL
);
ALTER TABLE "interests" ADD CONSTRAINT "PK_INTERESTS" PRIMARY KEY ("id");

-- 관심사 키워드
CREATE TABLE "interest_keywords" (
                                     "id"			uuid			NOT NULL,
                                     "keyword"		varchar(255)	NOT NULL,
                                     "interest_id"	uuid			NOT NULL
);
ALTER TABLE "interest_keywords" ADD CONSTRAINT "PK_INTEREST_KEYWORDS" PRIMARY KEY ("id");
ALTER TABLE "interest_keywords" ADD CONSTRAINT fk_interest_keywords_interests FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE CASCADE;
ALTER TABLE "interest_keywords" ADD CONSTRAINT uk_interest_keywords_interest_keyword UNIQUE ("interest_id", "keyword");


-- 알림
CREATE TABLE "notifications" (
                                 "id"				uuid			NOT NULL,
                                 "content"			varchar(100)	NOT NULL,
                                 "resource_id"		uuid			NOT NULL,
                                 "resource_type"		varchar(30)		NOT NULL,
                                 "confirmed"			boolean			DEFAULT false	NOT NULL,
                                 "created_at"		timestamp		DEFAULT NOW() NOT NULL,
                                 "updated_at"		timestamp		NULL,
                                 "user_id"			uuid			NOT NULL
);
ALTER TABLE "notifications" ADD CONSTRAINT "PK_NOTIFICATIONS" PRIMARY KEY ("id");
ALTER TABLE "notifications" ADD CONSTRAINT chk_resource_type CHECK (resource_type IN ('INTEREST', 'COMMENT'));
ALTER TABLE "notifications" ADD CONSTRAINT fk_notifications_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;


-- 뉴스 기사
CREATE TABLE "articles" (
                            "id"			uuid			NOT NULL,
                            "source"		varchar(30)		NOT NULL,
                            "source_url"	varchar(255)	NOT NULL,
                            "title"			varchar(255)	NOT NULL,
                            "publish_date"	timestamp		NOT NULL,
                            "summary"		text			NOT NULL,
                            "created_at"	timestamp		DEFAULT NOW() NOT NULL,
                            "deleted_at"	timestamp		NULL
);
ALTER TABLE "articles" ADD CONSTRAINT "PK_ARTICLES" PRIMARY KEY ("id");
ALTER TABLE "articles" ADD CONSTRAINT chk_source CHECK (source IN ('NAVER', 'HANKYUNG', 'CHOSUN', 'YEONHAP'));
ALTER TABLE "articles" ADD CONSTRAINT uk_articles_source_url UNIQUE ("source_url");

-- 관심사-구독자(유저)
CREATE TABLE "interest_users" (
                                  "id"			uuid		NOT NULL,
                                  "interest_id"	uuid		NOT NULL,
                                  "user_id"		uuid		NOT NULL,
                                  "created_at"	timestamp	DEFAULT NOW() NOT NULL
);
ALTER TABLE "interest_users" ADD CONSTRAINT "PK_INTEREST_USERS" PRIMARY KEY ("id");
ALTER TABLE "interest_users" ADD CONSTRAINT fk_interest_users_interests FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE CASCADE;
ALTER TABLE "interest_users" ADD CONSTRAINT fk_interest_users_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE "interest_users" ADD CONSTRAINT uk_interest_users_interest_users UNIQUE ("interest_id", "user_id");


-- 댓글
CREATE TABLE "comments" (
                            "id"			uuid			NOT NULL,
                            "content"		text			NOT NULL,
                            "created_at"	timestamp		DEFAULT NOW() NOT NULL,
                            "updated_at"	timestamp		NULL,
                            "user_id"		uuid			NOT NULL,
                            "article_id"	uuid			NOT NULL
);

ALTER TABLE "comments" ADD CONSTRAINT "PK_COMMENTS" PRIMARY KEY ("id");
ALTER TABLE "comments" ADD CONSTRAINT fk_comments_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE "comments" ADD CONSTRAINT fk_comments_articles FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE;


-- 사용자-기사 조회
CREATE TABLE "article_users" (
                                 "id"			uuid			NOT NULL,
                                 "user_id"		uuid			NOT NULL,
                                 "article_id"	uuid			NOT NULL,
                                 "created_at"	timestamp		DEFAULT NOW() NOT NULL
);
ALTER TABLE "article_users" ADD CONSTRAINT "PK_ARTICLE_USERS" PRIMARY KEY ("id");
ALTER TABLE "article_users" ADD CONSTRAINT fk_article_users_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE "article_users" ADD CONSTRAINT fk_article_users_articles FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE;
ALTER TABLE "article_users" ADD CONSTRAINT uk_article_users UNIQUE ("user_id", "article_id");



-- 사용자-댓글 좋아요
CREATE TABLE "comment_user_likes" (
                                      "id"			uuid		NOT NULL,
                                      "user_id"		uuid		NOT NULL,
                                      "comment_id"	uuid		NOT NULL,
                                      "created_at"	timestamp	DEFAULT NOW() NOT NULL
);
ALTER TABLE "comment_user_likes" ADD CONSTRAINT "PK_COMMENT_USER_LIKES" PRIMARY KEY ("id");
ALTER TABLE "comment_user_likes" ADD CONSTRAINT fk_comment_user_likes_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE "comment_user_likes" ADD CONSTRAINT fk_comment_user_likes_comments FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE;
ALTER TABLE "comment_user_likes" ADD CONSTRAINT uk_comment_user_likes UNIQUE ("user_id", "comment_id");

-- 조회 인덱스
CREATE INDEX idx_comments_user_id ON comments(user_id);
CREATE INDEX idx_comments_article_id ON comments(article_id);
CREATE INDEX idx_article_users_user_id ON article_users(user_id);
CREATE INDEX idx_article_users_article_id ON article_users(article_id);
CREATE INDEX idx_comment_user_likes_user_id ON comment_user_likes(user_id);
CREATE INDEX idx_comment_user_likes_comment_id ON comment_user_likes(comment_id);
CREATE INDEX idx_interest_users_user_id ON interest_users(user_id);
CREATE INDEX idx_interest_users_interest_id ON interest_users(interest_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_interest_keywords_interest_id ON interest_keywords(interest_id);

-- 댓글 테이블에 deleted_at 칼럼 추가
ALTER TABLE "comments" ADD COLUMN "deleted_at" timestamp NULL;

-- 기사 테이블에 updated_at, view_count, comment_count 칼럼 추가
ALTER TABLE "articles" ADD COLUMN "updated_at" timestamp NULL;
ALTER TABLE "articles"
    ADD COLUMN "view_count" BIGINT NOT NULL DEFAULT 0,
ADD COLUMN "comment_count" BIGINT NOT NULL DEFAULT 0;

-- 기사 테이블명 수정 및 관련인덱스 제약조건, 인덱스 수정
DROP TABLE article_users;

-- 사용자-기사 조회
CREATE TABLE "article_views" (
                                 "id"			uuid			NOT NULL,
                                 "user_id"		uuid			NOT NULL,
                                 "article_id"	uuid			NOT NULL,
                                 "created_at"	timestamp		DEFAULT NOW() NOT NULL
);

-- 제약조건 추가
ALTER TABLE "article_views" ADD CONSTRAINT "PK_ARTICLE_VIEWS" PRIMARY KEY ("id");
ALTER TABLE "article_views" ADD CONSTRAINT fk_article_views_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE "article_views" ADD CONSTRAINT fk_article_views_articles FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE;
ALTER TABLE "article_views" ADD CONSTRAINT uk_article_views UNIQUE ("user_id", "article_id");

-- 조회 인덱스
CREATE INDEX idx_article_views_user_id ON article_views(user_id);
CREATE INDEX idx_article_views_article_id ON article_views(article_id);

-- article source_url 컬럼 변경
ALTER TABLE articles ALTER COLUMN source_url TYPE TEXT;

-- interest unique 제약조건 추가
ALTER TABLE "interests" ADD CONSTRAINT uk_interests_name UNIQUE ("name");