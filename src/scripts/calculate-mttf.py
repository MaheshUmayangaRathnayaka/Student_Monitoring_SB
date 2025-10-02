class MTTFCalculator:
    def __init__(self):
        self.test_cycles = []
    
    def add_test_cycle(self, cycle_name, operating_hours, failures, test_cases):
        """Add a test cycle data point"""
        self.test_cycles.append({
            'name': cycle_name,
            'operating_hours': operating_hours,
            'failures': failures,
            'test_cases': test_cases
        })
    
    def calculate_mttf(self):
        """Calculate overall MTTF"""
        total_operating_time = sum(cycle['operating_hours'] for cycle in self.test_cycles)
        total_failures = sum(cycle['failures'] for cycle in self.test_cycles)
        
        if total_failures == 0:
            return float('inf')  # No failures
        
        mttf = total_operating_time / total_failures
        return mttf
    
    def calculate_failure_rate(self):
        """Calculate failure rate (failures per hour)"""
        mttf = self.calculate_mttf()
        if mttf == float('inf'):
            return 0
        return 1 / mttf
    
    def generate_report(self):
        """Generate MTTF analysis report"""
        total_operating_time = sum(cycle['operating_hours'] for cycle in self.test_cycles)
        total_failures = sum(cycle['failures'] for cycle in self.test_cycles)
        total_test_cases = sum(cycle['test_cases'] for cycle in self.test_cycles)
        mttf = self.calculate_mttf()
        failure_rate = self.calculate_failure_rate()
        
        print("=== MTTF Analysis Report ===")
        print(f"Total Test Cycles: {len(self.test_cycles)}")
        print(f"Total Operating Time: {total_operating_time} hours")
        print(f"Total Failures: {total_failures}")
        print(f"Total Test Cases: {total_test_cases}")
        print(f"MTTF: {mttf:.2f} hours")
        print(f"Failure Rate: {failure_rate:.6f} failures/hour")
        print(f"Reliability (24h): {(1 - (24 * failure_rate)) * 100:.2f}%")
        
        # Cycle-by-cycle analysis
        print("\n=== Cycle Analysis ===")
        for i, cycle in enumerate(self.test_cycles, 1):
            cycle_failure_rate = cycle['failures'] / cycle['operating_hours'] if cycle['operating_hours'] > 0 else 0
            print(f"Cycle {i} ({cycle['name']}):")
            print(f"  Operating Time: {cycle['operating_hours']} hours")
            print(f"  Failures: {cycle['failures']}")
            print(f"  Failure Rate: {cycle_failure_rate:.6f} failures/hour")

if __name__ == "__main__":
    # Initialize calculator
    calculator = MTTFCalculator()
    
    # Add test cycle data
    calculator.add_test_cycle("Week 1", 168, 2, 150)
    calculator.add_test_cycle("Week 2", 168, 1, 180)
    calculator.add_test_cycle("Week 3", 168, 3, 200)
    calculator.add_test_cycle("Week 4", 168, 1, 220)
    
    # Generate report
    calculator.generate_report()