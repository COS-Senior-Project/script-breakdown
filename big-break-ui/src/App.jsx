import { useState } from 'react'
import { Pencil, X, Grip } from "lucide-react";
import { DndContext, closestCenter, PointerSensor, useSensor, useSensors, rectIntersection } from "@dnd-kit/core";
import { arrayMove, SortableContext, verticalListSortingStrategy, useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import './App.css'

function App() {
    const [schedule, setSchedule] = useState([]);
    const [editingScene, setEditingScene] = useState(null);

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
            
            data.forEach(day => {
                day.scenes.forEach(scene =>{
                    scene.id = `${day.dayNumber}-${scene.sceneNumber}`
                });
            });

            setSchedule(data);
        } catch (error) {
            console.error(error)
        }
    };

    const addCharacter = (name) => {
        setEditingScene(prev => ({
            ...prev,
            canonicalCharacterNames: [...prev.canonicalCharacterNames.filter(n => n !== name), name],
            charactersBelowConfidence: prev.charactersBelowConfidence.filter((n) => n !== name)
        }));
    };

    //saves changes in the modal
    const saveScene = (updatedScene) => {
        const updatedSchedule = schedule.map((day) => ({
            ...day,
            scenes: day.scenes.map((scene) =>
                scene.sceneNumber === updatedScene.sceneNumber ? updatedScene : scene
            ),
        }));
        setSchedule(updatedSchedule);
        setEditingScene(null);
    }

    function SceneRow({scene, dayNumber, setEditingScene}) {
        const { attributes, listeners, setNodeRef, transform, transition } = useSortable({
            id: scene.id
        });

        const style={
            transform: CSS.Transform.toString(transform),
            transition
        };
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
            <tr
                ref={setNodeRef}
                style={{
                    ...style,
                    backgroundColor,
                    color: textColor
                }}
            >
                <td
                    {...attributes}
                    {...listeners}
                    style={{
                        cursor: "grab",
                        width: "30px",
                        textAlign: "center"
                    }}
                >
                    <Grip size={16}/>
                </td>
                <td>{scene.sceneNumber}</td>
                <td>{scene.heading}</td>
                <td>{scene.locationKeyword}</td>
                <td>{scene.location}</td>
                <td>{scene.shootPhase}</td>
                <td>{scene.pageCount}</td>
                <td>{scene.canonicalCharacterNames?.join(", ")}</td>
                <td>{scene.charactersBelowConfidence?.join(", ")}</td>
                {/* <td>{scene.charactersWithScores?.join(", ")}</td> */}
                <td style={{ textAlign: "center" }}>
                    <button onClick={(e) => {
                        e.stopPropagation();
                        setEditingScene({...scene, dayNumber: dayNumber, dayIndex: dayNumber - 1});
                    }}
                        style={{
                            background: "none",
                            border: "none",
                            cursor: "pointer",
                        }}
                    >
                    <Pencil size={16}/>     
                    </button>
                </td>
            </tr>
        );
    }

    const handleDragEnd = (event) => {
        const { active, over } = event;
        if (!over) return;

        const newSchedule = schedule.map(day => ({
            ...day,
            scenes: [...day.scenes]
        }));

        let activeScene, fromDay;

        let overDayNum;
        let overSceneNum;

        newSchedule.forEach(day => {
            const idx = day.scenes.findIndex(s => s.id === active.id);
            if (idx !== -1) {
                activeScene = day.scenes[idx];
                fromDay = day;
            }
        });

        const oldIndex = fromDay.scenes.findIndex(s => s.id === active.id);
        fromDay.scenes.splice(oldIndex, 1);

        let toDay;
        let newIndex = 0;

        newSchedule.forEach(day => {
            const idx = day.scenes.findIndex(s => s.id === over.id);
            if (idx !== -1) {
                toDay = day;
                newIndex = idx;
            }
        });

        toDay.scenes.splice(newIndex, 0, activeScene);
        setSchedule(newSchedule);
    };

    const sensors = useSensors(
        useSensor(PointerSensor)
    );

    return (
        <div>
            <h2>Upload Script</h2>

            <input
                type="file"
                onChange={(e) => handleUpload(e.target.files[0])}
            />

            <DndContext
                sensors={sensors}
                collisionDetection={rectIntersection}
                onDragEnd={handleDragEnd}
            >
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
                                        <th>Move</th>
                                        <th>Scene Number</th>
                                        <th>Heading</th>
                                        <th>INT/EXT</th>
                                        <th>Location</th>
                                        <th>Scene Shooting Time</th>
                                        <th>Scene Length</th>
                                        <th>Characters</th>
                                        <th>Possible Characters</th>
                                        <th>Edit</th>
                                    </tr>
                                </thead>
                                <SortableContext
                                    id={`day-${day.dayNumber}`}
                                    items={day.scenes.map(scene => `${day.dayNumber}-${scene.sceneNumber}`)}
                                    strategy={verticalListSortingStrategy}
                                >
                                    <tbody>
                                        {day.scenes.map((scene) => {
                                            return (
                                                <SceneRow
                                                    key={`${day.dayNumber}-${scene.sceneNumber}` }
                                                    scene={scene}
                                                    dayNumber={day.dayNumber}
                                                    setEditingScene={setEditingScene} 
                                                />     
                                            )
                                                                            
                                        })}
                                    </tbody>
                                </SortableContext>
                            </table>
                        </div>
                    ))}
                </div>
            </DndContext>
         

            {editingScene && (
                <div
                    style={{
                        position: "fixed",
                        top: "50%",
                        left: "50%",
                        transform: "translate(-50%, -50%)",
                        backgroundColor: "#ffffff",
                        border: "1px solid #ccc",
                        borderRadius: "8px",
                        padding: "12px",
                        width: "300px",
                        boxShadow: "0 4px 12px #1d1d1d",
                        zIndex: 1000,
                        fontSize: "14px"
                    }}
                >
                    <div style={{
                        display: "flex",
                        justifyContent: "space-between",
                        marginBottom: "8px"
                    }}
                    >
                        <h4 style={{ margin: 0 }}>Character Suggestions</h4>
                        
                        <button
                            style={{
                                border: "none",
                                background: "transparent",
                                cursor: "pointer",
                                fontWeight: "bold"
                            }}
                            onClick={() => setEditingScene(null)}
                        >
                            <X size={16}/>
                        </button>
                    </div>
                    <div>
                        <label>Characters:</label> <br/>
                        <input
                            value={editingScene.canonicalCharacterNames.join(", ")}
                            onChange={(e) =>
                                setEditingScene({
                                    ...editingScene,
                                    canonicalCharacterNames: e.target.value
                                    .split(", ")
                                    .map((n) => n.trim()),
                                })
                            }
                            style={{
                                marginBottom: "10px",
                                marginTop: "4px",
                                width: "95%",
                                height: "16px",
                                padding: "5px",
                                border: "1px solid #444444",
                                borderRadius: "6px"
                            }}
                        />
                    </div>

                    {editingScene.charactersBelowConfidence.length === 0 ? (
                        <div style={{ fontStyle: "italic", color: "#000000", marginBottom: "5px" }}>No character suggestions</div>
                    ) : (
                        editingScene.charactersBelowConfidence.map(name => (
                            <div
                                key={name}
                                onClick={() => addCharacter(name)}
                                style={{
                                    padding: "4px 6px",
                                    marginBottom: "4px",
                                    borderRadius: "4px",
                                    cursor: "pointer",
                                    backgroundColor: "#f0f0f0",
                                    transition: "background 0.2s"
                                }}
                                onMouseEnter={e => e.currentTarget.style.backgroundColor = "#e0e0ff"}
                                onMouseLeave={e => e.currentTarget.style.backgroundColor = "#f0f0f0"}
                            >
                                {name} 
                            </div>
                        ))
                    )}
                    <button onClick={() => saveScene(editingScene)} style={{ marginRight: "3px" }}>Save</button>
                    <button onClick={() => setEditingScene(null)} style={{ marginLeft: "3px" }}>Cancel</button>
                </div>
            )}
        </div>
    );
}

export default App
