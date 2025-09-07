param(
  [string]$Db = "debatehub",
  [string]$User = "debate_user",
  [string]$Host = "localhost",
  [int]$Port = 5432,
  [string]$OutFile = "..\db\debatehub.sql",
  [string]$PgDump = "C:\Program Files\PostgreSQL\17\bin\pg_dump.exe" 
)

$ErrorActionPreference = "Stop"
mkdir "..\db" -Force | Out-Null

if (-not $env:DB_PASSWORD) {
  throw "Set DB_PASSWORD env var first:  `setx DB_PASSWORD ""ChangeMe_Local_Only_123""`  (reopen the terminal after setx)"
}

$env:PGPASSWORD = $env:DB_PASSWORD
& $PgDump -h $Host -p $Port -U $User -d $Db -F p -f $OutFile
Write-Host "Dump written to $OutFile"
