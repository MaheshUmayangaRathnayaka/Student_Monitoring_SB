# PowerShell MTTF Calculator for Software Testing
# Alternative to Python script for Windows systems

class MTTFCalculator {
    [System.Collections.ArrayList]$TestCycles
    
    MTTFCalculator() {
        $this.TestCycles = @()
    }
    
    [void]AddTestCycle([string]$CycleName, [int]$OperatingHours, [int]$Failures, [int]$TestCases) {
        $cycle = @{
            Name = $CycleName
            OperatingHours = $OperatingHours
            Failures = $Failures
            TestCases = $TestCases
        }
        $this.TestCycles.Add($cycle) | Out-Null
    }
    
    [double]CalculateMTTF() {
        $totalOperatingTime = ($this.TestCycles | Measure-Object -Property OperatingHours -Sum).Sum
        $totalFailures = ($this.TestCycles | Measure-Object -Property Failures -Sum).Sum
        
        if ($totalFailures -eq 0) {
            return [double]::PositiveInfinity
        }
        
        return $totalOperatingTime / $totalFailures
    }
    
    [double]CalculateFailureRate() {
        $mttf = $this.CalculateMTTF()
        if ([double]::IsInfinity($mttf)) {
            return 0
        }
        return 1 / $mttf
    }
    
    [void]GenerateReport() {
        $totalOperatingTime = ($this.TestCycles | Measure-Object -Property OperatingHours -Sum).Sum
        $totalFailures = ($this.TestCycles | Measure-Object -Property Failures -Sum).Sum
        $totalTestCases = ($this.TestCycles | Measure-Object -Property TestCases -Sum).Sum
        $mttf = $this.CalculateMTTF()
        $failureRate = $this.CalculateFailureRate()
        $reliability24h = (1 - (24 * $failureRate)) * 100
        
        Write-Host "=== MTTF Analysis Report ===" -ForegroundColor Yellow
        Write-Host "Total Test Cycles: $($this.TestCycles.Count)"
        Write-Host "Total Operating Time: $totalOperatingTime hours"
        Write-Host "Total Failures: $totalFailures"
        Write-Host "Total Test Cases: $totalTestCases"
        Write-Host "MTTF: $($mttf.ToString('F2')) hours"
        Write-Host "Failure Rate: $($failureRate.ToString('F6')) failures/hour"
        Write-Host "Reliability (24h): $($reliability24h.ToString('F2'))%"
        
        Write-Host "`n=== Cycle Analysis ===" -ForegroundColor Yellow
        for ($i = 0; $i -lt $this.TestCycles.Count; $i++) {
            $cycle = $this.TestCycles[$i]
            $cycleFailureRate = if ($cycle.OperatingHours -gt 0) { $cycle.Failures / $cycle.OperatingHours } else { 0 }
            
            Write-Host "Cycle $($i + 1) ($($cycle.Name)):" -ForegroundColor Cyan
            Write-Host "  Operating Time: $($cycle.OperatingHours) hours"
            Write-Host "  Failures: $($cycle.Failures)"
            Write-Host "  Failure Rate: $($cycleFailureRate.ToString('F6')) failures/hour"
        }
    }
}

# Initialize calculator and run analysis
Write-Host "🔧 MTTF Calculator - Student Monitoring System" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green

$calculator = [MTTFCalculator]::new()

# Add test cycle data based on actual test results
$calculator.AddTestCycle("Week 1", 168, 2, 150)
$calculator.AddTestCycle("Week 2", 168, 1, 180)
$calculator.AddTestCycle("Week 3", 168, 3, 200)
$calculator.AddTestCycle("Week 4", 168, 1, 220)

# Generate and display report
$calculator.GenerateReport()

Write-Host "`n📊 Analysis Summary:" -ForegroundColor Green
$mttf = $calculator.CalculateMTTF()
if ($mttf -lt 50) {
    Write-Host "❌ POOR: MTTF below 50 hours - Critical improvement needed" -ForegroundColor Red
} elseif ($mttf -lt 100) {
    Write-Host "⚠️ AVERAGE: MTTF 50-100 hours - Improvement recommended" -ForegroundColor Yellow
} elseif ($mttf -lt 200) {
    Write-Host "✅ GOOD: MTTF 100-200 hours - Acceptable performance" -ForegroundColor Green
} else {
    Write-Host "🏆 EXCELLENT: MTTF above 200 hours - Outstanding reliability" -ForegroundColor Green
}

Write-Host "`n💡 Next Steps:" -ForegroundColor Cyan
Write-Host "1. Document current MTTF in Quality Metrics report"
Write-Host "2. Set target MTTF improvement goals"
Write-Host "3. Track weekly trends in test execution logs"
Write-Host "4. Correlate with defect density metrics"