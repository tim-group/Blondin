require "webrick"

MAX_ALLOWED_SLOW_REQUESTS = ARGV.shift.to_i

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
      response.body = "Slow with concurrently #{@@slow_requests} requests"
      @@mutex.synchronize { @@slow_requests -= 1 }
      return
    end

    response.body = 'Fast'
    sleep 0.5
  end
  
end

webrickServer = WEBrick::HTTPServer.new(:Port => 8081)
webrickServer.mount '/', Server
trap 'INT' do webrickServer.shutdown end
webrickServer.start