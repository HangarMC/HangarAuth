from django.shortcuts import render, redirect
from django.urls import reverse
from django.contrib.auth.decorators import login_required
from django.utils.http import urlencode
from django.conf import settings


@login_required
def index(request):
    context = {'hangar_host': settings.HANGAR_HOST}
    return render(request, "core/index.html", context)


def admin_login_redirect(request):
    if request.user.is_authenticated and not request.user.is_staff:
        return redirect("index")

    return redirect("{}?{}".format(reverse("accounts:login"), urlencode({"next": request.GET.get("next", "/")})))
