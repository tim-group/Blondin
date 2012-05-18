require "net/http"

def request(path)
  http = Net::HTTP.new("localhost", 8082)
  http.read_timeout = 500
  response = http.get(path)

  print '.'
  if response.code != '200'
    puts "Response was #{response.code}"
    exit 1
  end
end

thread_number = 100
request_number = 10

threads = []

print 'Testing'

thread_number.times do |thread_id|
  threads << Thread.new do
    request("/slow") 
  end
end

thread_number.times do |thread_id|
  threads << Thread.new do
    request_number.times { request("/fast") }
  end
end

threads.each { |thread| thread.join }

puts "Passed"