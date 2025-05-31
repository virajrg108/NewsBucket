import { Newspaper, Wifi } from "lucide-react";

function App() {
  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <Newspaper className="material-blue text-3xl mr-3 h-8 w-8" />
              <h1 className="text-xl font-medium text-gray-900">News Bucket</h1>
            </div>
            <div className="flex items-center space-x-4">
              { /*//TODO: After implementing offline functionality, toggle this to WifiOff when offline */}
              <div className="flex items-center text-sm text-gray-500">
                  <Wifi className="text-green-500 h-5 w-5 mr-1" /> Connected
              </div>
            </div>
          </div>
        </div>
      </header>
    </div>
  )
}

export default App