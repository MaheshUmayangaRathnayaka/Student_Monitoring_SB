# Simple PowerShell MTTF Calculator
# Student Monitoring System Quality Metrics

Write-Host "🔧 MTTF Calculator - Student Monitoring System" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green

# Test cycle data
$testCycles = @(
    @{ Name = "Week 1"; OperatingHours = 168; Failures = 2; TestCases = 150 }
    @{ Name = "Week 2"; OperatingHours = 168; Failures = 1; TestCases = 180 }
    @{ Name = "Week 3"; OperatingHours = 168; Failures = 3; TestCases = 200 }
    @{ Name = "Week 4"; OperatingHours = 168; Failures = 1; TestCases = 220 }
)

# Calculate totals
$totalOperatingTime = ($testCycles | Measure-Object -Property OperatingHours -Sum).Sum
$totalFailures = ($testCycles | Measure-Object -Property Failures -Sum).Sum
$totalTestCases = ($testCycles | Measure-Object -Property TestCases -Sum).Sum

# Calculate MTTF
if ($totalFailures -eq 0) {
    $mttf = [double]::PositiveInfinity
    $failureRate = 0
} else {
    $mttf = $totalOperatingTime / $totalFailures
    $failureRate = 1 / $mttf
}

$reliability24h = if ($failureRate -eq 0) { 100 } else { (1 - (24 * $failureRate)) * 100 }

# Generate report
Write-Host "`n=== MTTF Analysis Report ===" -ForegroundColor Yellow
Write-Host "Total Test Cycles: $($testCycles.Count)"
Write-Host "Total Operating Time: $totalOperatingTime hours"
Write-Host "Total Failures: $totalFailures"
Write-Host "Total Test Cases: $totalTestCases"
Write-Host "MTTF: $($mttf.ToString('F2')) hours"
Write-Host "Failure Rate: $($failureRate.ToString('F6')) failures/hour"
Write-Host "Reliability (24h): $($reliability24h.ToString('F2'))%"

Write-Host "`n=== Cycle Analysis ===" -ForegroundColor Yellow
for ($i = 0; $i -lt $testCycles.Count; $i++) {
    $cycle = $testCycles[$i]
    $cycleFailureRate = if ($cycle.OperatingHours -gt 0) { $cycle.Failures / $cycle.OperatingHours } else { 0 }
    
    Write-Host "Cycle $($i + 1) ($($cycle.Name)):" -ForegroundColor Cyan
    Write-Host "  Operating Time: $($cycle.OperatingHours) hours"
    Write-Host "  Failures: $($cycle.Failures)"
    Write-Host "  Failure Rate: $($cycleFailureRate.ToString('F6')) failures/hour"
}

# Analysis and recommendations
Write-Host "`n📊 Quality Assessment:" -ForegroundColor Green
if ($mttf -lt 50) {
    Write-Host "❌ POOR: MTTF below 50 hours - Critical improvement needed" -ForegroundColor Red
    $status = "CRITICAL"
} elseif ($mttf -lt 100) {
    Write-Host "⚠️ AVERAGE: MTTF 50-100 hours - Improvement recommended" -ForegroundColor Yellow
    $status = "NEEDS_IMPROVEMENT"
} elseif ($mttf -lt 200) {
    Write-Host "✅ GOOD: MTTF 100-200 hours - Acceptable performance" -ForegroundColor Green
    $status = "GOOD"
} else {
    Write-Host "🏆 EXCELLENT: MTTF above 200 hours - Outstanding reliability" -ForegroundColor Green
    $status = "EXCELLENT"
}

# Industry benchmarks
Write-Host "`n🎯 Industry Benchmarks:" -ForegroundColor Magenta
Write-Host "  • Critical Systems: > 8760 hours (1 year)"
Write-Host "  • Production Systems: > 2190 hours (3 months)"
Write-Host "  • Development Systems: > 168 hours (1 week)"
Write-Host "  • Current System: $($mttf.ToString('F2')) hours"

Write-Host "`n📈 Improvement Targets:" -ForegroundColor Cyan
$targetMTTF = $mttf * 2
Write-Host "  • Short-term target: $($targetMTTF.ToString('F2')) hours"
Write-Host "  • Long-term target: 200+ hours"
Write-Host "  • Required failure reduction: $((($totalFailures - ($totalOperatingTime / $targetMTTF)) / $totalFailures * 100).ToString('F1'))%"

Write-Host "`n💡 Recommendations:" -ForegroundColor Yellow
switch ($status) {
    "CRITICAL" {
        Write-Host "1. 🚨 IMMEDIATE: Fix all critical bugs (SMS-001, SMS-004)"
        Write-Host "2. 🔧 Add comprehensive error handling"
        Write-Host "3. 📝 Implement automated monitoring"
        Write-Host "4. 🧪 Increase test coverage to 85%+"
    }
    "NEEDS_IMPROVEMENT" {
        Write-Host "1. 🐛 Address major defects systematically"
        Write-Host "2. 📊 Improve test coverage to 80%+"
        Write-Host "3. 🔍 Add integration testing"
        Write-Host "4. 📈 Track metrics weekly"
    }
    "GOOD" {
        Write-Host "1. ✨ Focus on code quality improvements"
        Write-Host "2. 🚀 Optimize performance bottlenecks"
        Write-Host "3. 📋 Implement preventive measures"
        Write-Host "4. 📊 Maintain current quality standards"
    }
    "EXCELLENT" {
        Write-Host "1. 🏆 Maintain excellence standards"
        Write-Host "2. 📚 Document best practices"
        Write-Host "3. 🎯 Set stretch goals"
        Write-Host "4. 👥 Share knowledge with team"
    }
}

# Save results to file
$reportDate = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
$reportFile = "target\mttf-report-$reportDate.txt"

$reportContent = @"
MTTF Analysis Report - Generated $(Get-Date)
===============================================

Summary:
- Total Test Cycles: $($testCycles.Count)
- Total Operating Time: $totalOperatingTime hours
- Total Failures: $totalFailures
- Total Test Cases: $totalTestCases
- MTTF: $($mttf.ToString('F2')) hours
- Failure Rate: $($failureRate.ToString('F6')) failures/hour
- 24h Reliability: $($reliability24h.ToString('F2'))%
- Quality Status: $status

Cycle Details:
$($testCycles | ForEach-Object { "  $($_.Name): $($_.OperatingHours)h, $($_.Failures) failures, $($_.TestCases) tests" } | Out-String)

Recommendations: Focus on $status level improvements
"@

# Ensure target directory exists
if (!(Test-Path "target")) {
    New-Item -ItemType Directory -Path "target" -Force | Out-Null
}

$reportContent | Out-File -FilePath $reportFile -Encoding UTF8
Write-Host "`n💾 Report saved to: $reportFile" -ForegroundColor Green

Write-Host "`n🎯 Next Steps for Quality Metrics Tutorial:" -ForegroundColor Magenta
Write-Host "1. ✅ MTTF calculated: $($mttf.ToString('F2')) hours"
Write-Host "2. 📊 Document in defect density calculations"
Write-Host "3. 🔍 Run SonarQube analysis next"
Write-Host "4. 📈 Create quality dashboard"