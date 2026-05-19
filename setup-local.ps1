# Generates src/main/resources/application.properties from the .example template.
# Run once after cloning: .\setup-local.ps1

$target = "src/main/resources/application.properties"
$example = "src/main/resources/application.properties.example"

if (Test-Path $target) {
    Write-Host "application.properties already exists. Nothing to do." -ForegroundColor Yellow
    exit 0
}

if (-not (Test-Path $example)) {
    Write-Host "ERROR: $example not found." -ForegroundColor Red
    exit 1
}

Copy-Item $example $target
Write-Host "Created $target from example." -ForegroundColor Green
Write-Host "Open it and replace all YOUR_* placeholders with real credentials before starting the app." -ForegroundColor Cyan
