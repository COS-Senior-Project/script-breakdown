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
                    <div key={day.dayNumber} style={{ marginBottom: "40px" }}>
                        <h2>Day {day.dayNumber}</h2>

                        <div style={{ marginBottom: "20px", fontSize: "16px" }}>
                            <strong>Page length: </strong>{day.pageCount}<br/>
                            <strong>Time:</strong> {day.time}<br/>
                            <strong>Move Count:</strong> {day.moveCount}<br/>
                            <strong>Locations</strong>: {day.locationSet?.join(", ")}
                        </div>

                        <table border="1" cellPadding="4" style={{ borderCollapse: "collapse", width: "100%", fontSize: "14px" }}>
                            <thead>
                                <tr>
                                    <th>Scene Number</th>
                                    <th>Heading</th>
                                    <th>INT/EXT</th>
                                    <th>Location</th>
                                    <th>Scene Shooting Time</th>
                                    <th>Scene Length</th>
                                    <th>Characters</th>
                                    <th>Possible Characters</th>
                                </tr>
                            </thead>
                            <tbody>
                                {day.scenes.map((scene) => {
                                    const isINT = scene.locationKeyword === "INT";
                                    const isEXT = scene.locationKeyword === "EXT";
                                    const isDAY = scene.shootPhase === "DAY";
                                    const isNIGHT = scene.shootPhase === "NIGHT";

                                    let backgroundColor = "white";
                                    let textColor = "black";

                                    if (isINT && isDAY) {
                                        backgroundColor = "white";
                                    }
                                    else if (isEXT && isDAY) {
                                        backgroundColor = "#fef08a"
                                    }
                                    else if (isINT && isNIGHT) {
                                        backgroundColor = "#bbf7d0"
                                    }
                                    else if (isEXT && isNIGHT) {
                                        backgroundColor = "#1e3a8a"
                                        textColor = "white";
                                    }
                                    return (
                                        <tr key={`${day.dayNumber}-${scene.sceneNumber}`}
                                        style={{
                                            backgroundColor,
                                            color: textColor
                                        }}>
                                            <td>{scene.sceneNumber}</td>
                                            <td>{scene.heading}</td>
                                            <td>{scene.locationKeyword}</td>
                                            <td>{scene.location}</td>
                                            <td>{scene.shootPhase}</td>
                                            <td>{scene.pageCount}</td>
                                            <td>{scene.canonicalCharacterNames?.join(", ")}</td>
                                            <td>{scene.charactersBelowConfidence?.join(", ")}</td>
                                            {/* <td>{scene.charactersWithScores?.join(", ")}</td> */}
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default App
