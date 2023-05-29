{{- define "keycloak.url" -}}
{{- printf "%s%s" "https://" .Values.keycloak.host }}
{{- end -}}

{{- define "keycloak.externalSystemTargetRealm" -}}
{{- printf "%s-%s" .Values.namespace .Values.keycloak.trembitaInvokerClient.realm }}
{{- end -}}

{{- define "keycloak.urlPrefix" -}}
{{- printf "%s%s%s" .Values.keycloak.url "/auth/realms/" .Release.Namespace -}}
{{- end -}}

{{- define "issuer.external" -}}
{{- printf "%s-%s" (include "keycloak.urlPrefix" .) .Values.keycloak.trembitaInvokerClient.realm -}}
{{- end -}}

{{- define "external-system-api.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "external-system-api.metaLabels" -}}
app.kubernetes.io/name: external-system-api
helm.sh/chart: {{ template "external-system-api.chart" . }}
app.kubernetes.io/instance: "{{ .Release.Name }}"
app.kubernetes.io/managed-by: "{{ .Release.Service }}"
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/component: app
{{- end -}}

{{- define "external-system-api.hostname" -}}
{{- printf "external-service-api-%s" (include "edp.hostnameSuffix" .) }}
{{- end }}

{{- define "edp.hostnameSuffix" -}}
{{- printf "%s.%s" .Values.stageName .Values.dnsWildcard }}
{{- end }}

{{- define "trembita.whitelist.cidr" -}}
{{- if .Values.trembita.ipList }}
{{- $ipList := .Values.trembita.ipList }}
{{- $cidrList := (list) }}
{{- range $ipList }}
{{- $cidrList = append $cidrList (printf "%s/32" .) }}
{{- end }}
{{- join " " $cidrList }}
{{- end }}
{{- end -}}

{{- define "trembita.whitelist.annotation" -}}
haproxy.router.openshift.io/ip_whitelist: {{ include "trembita.whitelist.cidr" . }}
{{- end -}}

{{- define "admin-routes.whitelist.cidr" -}}
{{- if .Values.global.whiteListIP }}
{{- .Values.global.whiteListIP.adminRoutes }}
{{- end }}
{{- end -}}

{{- define "admin-routes.whitelist.annotation" -}}
haproxy.router.openshift.io/ip_whitelist: {{ (include "admin-routes.whitelist.cidr" . | default "0.0.0.0/0") | quote }}
{{- end -}}

{{- define "horizontalPodAutoscaler.apiVersion" }}
{{- if eq .Values.global.clusterVersion "4.9.0" }}
{{- printf "%s" "autoscaling/v2beta2" }}
{{- else }}
{{- printf "%s" "autoscaling/v2" }}
{{- end }}
{{- end }}
