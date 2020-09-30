from .base import *
import ast

GIT_REPO_ROOT = os.path.dirname(BASE_DIR)
PARENT_ROOT = os.path.dirname(GIT_REPO_ROOT)

DEBUG = os.environ["DEBUG"] == 'true'

SECRET_KEY = os.environ["SECRET_KEY"]

DEFAULT_FROM_EMAIL = "admin@papermc.io"
SERVER_EMAIL = "admin@papermc.io"

SESSION_COOKIE_SECURE = True
SESSION_COOKIE_HTTPONLY = True
CSRF_COOKIE_SECURE = True
CSRF_COOKIE_HTTPONLY = True

EMAIL_BACKEND = "django.core.mail.backends.smtp.EmailBackend"
EMAIL_USE_TLS = os.environ["EMAIL_TLS"] == 'true'
EMAIL_USE_SSL = os.environ["EMAIL_SSL"] == 'true'
EMAIL_HOST = os.environ["EMAIL_HOST"]
EMAIL_PORT = int(os.environ["EMAIL_PORT"])
EMAIL_HOST_USER = os.environ["EMAIL_HOST_USER"]
EMAIL_HOST_PASSWORD = os.environ["EMAIL_HOST_PASSWORD"]

TEMPLATES = [
    {
        "BACKEND": "django.template.backends.django.DjangoTemplates",
        "DIRS": [os.path.join(BASE_DIR, "templates")],
        "OPTIONS": {
            "loaders": [
                (
                    "django.template.loaders.cached.Loader",
                    ["django.template.loaders.filesystem.Loader", "django.template.loaders.app_directories.Loader"],
                )
            ],
            "context_processors": [
                "django.template.context_processors.debug",
                "django.template.context_processors.request",
                "django.contrib.auth.context_processors.auth",
                "django.contrib.messages.context_processors.messages",
            ],
        },
    }
]

for k, v in os.environ.items():
    if not k.startswith("SSO_ENDPOINT_"):
        continue
    k = k[len("SSO_ENDPOINT_"):]
    SSO_ENDPOINTS[k.lower()] = ast.literal_eval(v)


DATABASES = {
    "default": {
        "ENGINE": "django.db.backends.postgresql",
        "NAME": os.environ["DB_NAME"],
        "USER": os.environ["DB_USER"],
        "PASSWORD": os.environ["DB_PASSWORD"],
        "HOST": os.environ["DB_HOST"],
        "ATOMIC_REQUESTS": True,
    }
}

STATICFILES_STORAGE = "core.staticfiles.SourcemapManifestStaticFilesStorage"
STATIC_ROOT = os.path.join(PARENT_ROOT, "public_html", "static")
MEDIA_ROOT = os.path.join(PARENT_ROOT, "public_html", "media")

ACCOUNTS_AVATAR_CHANGE_GROUPS = ["dummy", "Ore_Organization"]

RQ_QUEUES = {"default": {"HOST": "hangar_redis", "PORT": 6379, "DB": 0, "DEFAULT_TIMEOUT": 300}}

# for queue in RQ_QUEUES.values():
#     queue["ASYNC"] = False
# from fakeredis import FakeRedis, FakeStrictRedis
# import django_rq.queues

# django_rq.queues.get_redis_connection = lambda _, strict: FakeStrictRedis() if strict else FakeRedis()
