# -*- coding: utf-8 -*-
# Creates groups to match Hangar's global roles.
from __future__ import unicode_literals

from django.db import migrations


roles = [
    "Hangar Admin",
    "Hangar Mod",

    "Paper Leader",
    "Team Leader",
    "Community Leader",

    "Paper Staff",
    "Paper Developer",

    "Hangar Dev",
    "Web Dev",

    "Documenter",
    "Support",
    "Contributor",
    "Advisor",

    "Stone Donor",
    "Quartz Donor",
    "Iron Donor",
    "Gold Donor",
    "Diamond Donor"
]


def forwards_func(apps, schema_editor):
    Group = apps.get_model("accounts", "Group")
    db_alias = schema_editor.connection.alias
    for role in roles:
        Group.objects.using(db_alias).bulk_create([Group(name=role, internal_name=role.replace(" ", "_"), internal_only=False)])


def reverse_func(apps, schema_editor):
    Group = apps.get_model("accounts", "Group")
    db_alias = schema_editor.connection.alias
    for role in roles:
        Group.objects.using(db_alias).filter(name=role).delete()


class Migration(migrations.Migration):

    dependencies = [("accounts", "0013_user_discord_id")]

    operations = [migrations.RunPython(forwards_func, reverse_func)]
