import pandas as pd
import matplotlib.pyplot as plt

# Read JMeter results
df = pd.read_csv('performance-tests/results.jtl')

# Calculate key metrics
avg_response_time = df['elapsed'].mean()
percentile_95 = df['elapsed'].quantile(0.95)
max_response_time = df['elapsed'].max()
error_rate = (df['success'] == False).sum() / len(df) * 100
throughput = len(df) / (df['timeStamp'].max() - df['timeStamp'].min()) * 1000

print(f"Performance Test Results:")
print(f"Average Response Time: {avg_response_time:.2f}ms")
print(f"95th Percentile: {percentile_95:.2f}ms")
print(f"Max Response Time: {max_response_time:.2f}ms")
print(f"Error Rate: {error_rate:.2f}%")
print(f"Throughput: {throughput:.2f} req/sec")

# Create response time graph
plt.figure(figsize=(12, 6))
plt.plot(df['timeStamp'], df['elapsed'])
plt.title('Response Time Over Time')
plt.xlabel('Time')
plt.ylabel('Response Time (ms)')
plt.savefig('performance-tests/response-time-graph.png')