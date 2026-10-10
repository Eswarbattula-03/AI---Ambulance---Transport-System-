/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          red: '#DC2626',
          darkred: '#991B1B',
          blue: '#2563EB',
          navy: '#1E3A8A',
        }
      }
    },
  },
  plugins: [],
}
