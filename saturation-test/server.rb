require "webrick"

class Server < WEBrick::HTTPServlet::AbstractServlet
  
  def do_GET(request, response)
    response.body = 'It works'
    sleep 1
  end
  
end

webrickServer = WEBrick::HTTPServer.new(:Port => 8081)
webrickServer.mount '/', Server
trap 'INT' do webrickServer.shutdown end
webrickServer.start