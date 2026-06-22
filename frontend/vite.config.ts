import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    {
      name: 'local-admin-page',
      configureServer(server) {
        server.middlewares.use((request, response, next) => {
          if (request.url === '/local-admin/' || request.url === '/local-admin') {
            response.statusCode = 302
            response.setHeader('Location', '/local-admin/index.html')
            response.end()
            return
          }

          next()
        })
      },
    },
  ],
})
