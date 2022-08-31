{{/*
Expand the name of the chart.
*/}}
{{- define "hangarauth.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "hangarauth.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "hangarauth.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "hangarauth.labels" -}}
helm.sh/chart: {{ include "hangarauth.chart" . }}
{{ include "hangarauth.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "hangarauth.selectorLabels" -}}
app.kubernetes.io/name: {{ include "hangarauth.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "hangarauth.frontend.serviceAccountName" -}}
{{- if .Values.frontend.serviceAccount.create }}
{{- default (printf "%s-frontend" (include "hangarauth.fullname" .)) .Values.frontend.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.frontend.serviceAccount.name }}
{{- end }}
{{- end }}

{{- define "hangarauth.backend.serviceAccountName" -}}
{{- if .Values.backend.serviceAccount.create }}
{{- default (printf "%s-backend" (include "hangarauth.fullname" .)) .Values.backend.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.backend.serviceAccount.name }}
{{- end }}
{{- end }}

{{- define "hangarauth.hydra.serviceAccountName" -}}
{{- if .Values.hydra.serviceAccount.create }}
{{- default (printf "%s-hydra" (include "hangarauth.fullname" .)) .Values.hydra.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.hydra.serviceAccount.name }}
{{- end }}
{{- end }}

{{- define "hangarauth.kratos.serviceAccountName" -}}
{{- if .Values.kratos.serviceAccount.create }}
{{- default (printf "%s-kratos" (include "hangarauth.fullname" .)) .Values.kratos.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.kratos.serviceAccount.name }}
{{- end }}
{{- end }}
