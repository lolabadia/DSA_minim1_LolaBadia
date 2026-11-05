const API_URL = "http://localhost:8080/dsaApp/tracks";

async function fetchTracks() {
    const container = document.getElementById('tracks');

    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const data = await response.json();
        container.innerHTML = "";

        data.forEach(track => {
            const card = document.createElement('div');
            card.classList.add('track-card');

            card.innerHTML = `
                    <h3>${track.title || 'Unknown Track'}</h3>
                    <p>${track.singer || 'Unknown Artist'}</p>
                    <small>Ref: ${track.id}</small>
                `;
            container.appendChild(card);
        });

    } catch (error) {
        console.error("Error al cargar tracks:", error);
        container.innerHTML = `<p style="color: red; text-align: center;">No se pudo conectar con la API en ${API_URL}</p>`;
    }
}

fetchTracks();