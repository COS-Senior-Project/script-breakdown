import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import heroImg from './assets/hero.png'
import './App.css'

function App() {
    const [schedule, setSchedule] = useState([]);

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

            setSchedule(data);
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

            <div style={{marginTop: "30px"}}>
                {schedule.map((day) => (
                    <div key={day.dayNumber} style={{ marginBottom: "20px" }}>
                        <h2>Day {day.dayNumber}</h2>
                        
                        Page length: {day.pageCount}<br/>
                        Time: {day.time}<br/>
                        Move Count: {day.moveCount}<br/>
                        Locations:<br/>
                        {day.locationSet.map((location) => (
                            <div key={location}>{location}</div>
                        ))}
                         

                        {day.scenes.map((scene) => (
                            <div key={scene.sceneNumber}>
                                <h4>Scene {scene.sceneNumber}</h4>
                                Scene Heading: {scene.heading}<br/>
                                INT/EXT: {scene.locationKeyword}<br/>
                                Location: {scene.location}<br/>
                                Scene Shooting Time: {scene.shootPhase}<br/>
                                Scene Length: {scene.pageCount}<br/>
                                Characters:<br/>
                                {scene.canonicalCharacterNames.map((character) => (
                                    <div key={character}>{character}</div>
                                ))}
                            </div>
                        ))}
                    </div>
                ))}
            </div>
        </div>
    );
}

export default App
