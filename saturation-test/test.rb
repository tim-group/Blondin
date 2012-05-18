require "net/http"

THREAD_POOL = []

def for_all_threads(&blk)
  100.times { THREAD_POOL << Thread.new(&blk) }
end

def request(path)
  http = Net::HTTP.new("localhost", 8082)
  http.read_timeout = 500
  response = http.get(path)

  print '.'

  if response.code != '200'
    puts "Failed. Response was #{response.code}"
    exit 1
  end
end

print 'Testing'

for_all_threads { request("/slow") }
for_all_threads { 10.times { request("/fast") } }

THREAD_POOL.each { |thread| thread.join }

puts "Passed"