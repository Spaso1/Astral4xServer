param (
    [string]$processName,
    [int]$interval = 5
)

function Get-ProcessIdByName {
    param ([string]$name)
    return (Get-Process -Name $name -ErrorAction SilentlyContinue).Id
}

$processId = Get-ProcessIdByName -name $processName

if (-not $processId) {
    Write-Output "Process '$processName' not found."
    exit
}

Write-Output "Monitoring network traffic for process ID: $processId"

while ($true) {
    $connections = Get-NetTCPConnection -OwningProcess $processId | Where-Object { $_.State -eq "Established" }
    if ($connections) {
        foreach ($connection in $connections) {
            $localAddress = $connection.LocalAddress
            $localPort = $connection.LocalPort
            $remoteAddress = $connection.RemoteAddress
            $remotePort = $connection.RemotePort
            $state = $connection.State

            Write-Output "Local: $localAddress:$localPort -> Remote: $remoteAddress:$remotePort, State: $state"
        }
    } else {
        Write-Output "No active connections for process ID: $processId"
    }

    Start-Sleep -Seconds $interval
}
