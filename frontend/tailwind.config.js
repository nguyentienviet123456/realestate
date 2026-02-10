/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#1a56db',
        'status-done': '#10b981',
        'status-pending': '#f59e0b',
      }
    },
  },
  plugins: [],
}
