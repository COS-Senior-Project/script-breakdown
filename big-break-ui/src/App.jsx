import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import heroImg from './assets/hero.png'
import './App.css'

function App() {
  const handleUpload = async (file) => {
      const formData = new FormData();
      formData.append("file", file);

      try {
          const response = await fetch("http://localhost:8080/api/upload-script", {
              method: "POST",
              body: formData
          });

          if (!response.ok) {
              throw new Error ("Upload failed");
          }

          const data = await response.json();
          console.log(data);
      } catch (error) {
          console.error(error)
      }
  };

  return (
      <div>
          <h2>Upload Script</h2>

          <input
              type="file"
              onChange={(e) => handleUpload(e.target.files[0])}
          />
      </div>
  );
}

export default App
