# Genera src/main/resources/application-local.properties desde la plantilla,
# con un jwt.secret aleatorio de 384 bits ya puesto.
#
# Uso, una vez tras clonar:
#     .\setup-local.ps1
#
# Y para arrancar la aplicacion con esa configuracion:
#     .\mvnw spring-boot:run -Dspring-boot.run.profiles=local
#
# El fichero generado esta en .gitignore. Es el unico con valores reales.

$ErrorActionPreference = "Stop"

$target  = "src/main/resources/application-local.properties"
$example = "src/main/resources/application-local.properties.example"

if (Test-Path $target) {
    Write-Host "$target ya existe. No se toca nada." -ForegroundColor Yellow
    Write-Host "Si quieres regenerarlo, borralo primero." -ForegroundColor Yellow
    exit 0
}

if (-not (Test-Path $example)) {
    Write-Host "ERROR: no se encuentra $example" -ForegroundColor Red
    exit 1
}

# 48 bytes -> 64 caracteres en base64, muy por encima del minimo de 32 bytes de HS256.
$bytes = New-Object byte[] 48
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
$jwtSecret = [Convert]::ToBase64String($bytes)

(Get-Content $example -Raw).Replace('GENERATED_BY_SETUP_SCRIPT', $jwtSecret) |
    Set-Content $target -Encoding utf8

Write-Host "Creado $target" -ForegroundColor Green
Write-Host "jwt.secret generado automaticamente (384 bits)." -ForegroundColor Green
Write-Host ""

$pending = Select-String -Path $target -Pattern 'YOUR_[A-Z_]+' -AllMatches
if ($pending) {
    Write-Host "Rellena estos placeholders antes de arrancar:" -ForegroundColor Cyan
    $pending | ForEach-Object { "  linea $($_.LineNumber): $($_.Line.Trim())" }
    Write-Host ""
    Write-Host "La aplicacion se negara a arrancar si falta alguno. Es intencionado:" -ForegroundColor Cyan
    Write-Host "un arranque con secretos por defecto es peor que uno fallido." -ForegroundColor Cyan
} else {
    Write-Host "No quedan placeholders pendientes." -ForegroundColor Green
}

Write-Host ""
Write-Host "Arranca con:  .\mvnw spring-boot:run -Dspring-boot.run.profiles=local" -ForegroundColor White
