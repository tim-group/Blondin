require "net/http"

threads = []

uri = URI('http://localhost:8082')

100.times do |thread_id|
  threads << Thread.new do
    10.times do |request_id|
      response = Net::HTTP.get(uri)
      puts "Thread #{thread_id} (#{request_id}): response"
    end
  end
end

threads.each { |thread| thread.join }