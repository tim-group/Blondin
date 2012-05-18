require "webrick"

MAX_ALLOWED_SLOW_REQUESTS = ARGV.shift.to_i
BIG_CONTENT = Array.new(100000, 'a').join

class Server < WEBrick::HTTPServlet::AbstractServlet
  @@slow_requests = 0
  @@mutex = Mutex.new
  
  def do_GET(request, response)
    if request.request_uri.path == "/slow"
      @@mutex.synchronize do
        @@slow_requests += 1
        response.status = 500 if @@slow_requests > MAX_ALLOWED_SLOW_REQUESTS
      end

      sleep 1
      @@mutex.synchronize { @@slow_requests -= 1 }
    else
      sleep 0.5
    end

    response.body = BIG_CONTENT
  end
  
end

webrickServer = WEBrick::HTTPServer.new(:Port => 8081, :AccessLog => [])
webrickServer.mount '/', Server
trap('INT') { webrickServer.shutdown }
webrickServer.start