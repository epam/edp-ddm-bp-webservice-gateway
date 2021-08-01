{{- define "keycloak.url" -}}
{{- printf "%s%s" "https://" .Values.keycloak.host }}
{{- end -}}

{{- define "keycloak.externalSystemTargetRealm" -}}
{{- printf "%s-%s" .Values.namespace .Values.keycloak.trembitaInvokerClient.realm }}
{{- end -}}