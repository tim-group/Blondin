require "net/http"

uri = URI('http://localhost:8082')

thread_number = 100
request_number = 10

threads = []

thread_number.times do |thread_id|
  threads << Thread.new do
    request_number.times do |request_id|
      response = Net::HTTP.get_response(uri)
      raise "Response was #{response.code}" if response.code != '200'
      puts "Thread #{thread_id} (#{request_id}): #{response.body} (status: #{response.code})"
    end
  end
end

threads.each { |thread| thread.join }