import numpy as np
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation
from mpl_toolkits.mplot3d import Axes3D

# Stałe fizyczne
q = 1e-9  # Ładunek w kulombach
epsilon_0 = 8.85e-12  # Przenikalność elektryczna próżni
c = 3e8  # Prędkość światła w m/s

# Wektory prędkości i przyspieszenia (przykładowe wartości)
u = np.array([0.5, 0.2, 0])  # Prędkość w 3D (jednostki względne)
a = np.array([0, 0.1, 0.2])  # Przyspieszenie w 3D (jednostki względne)

# Siatka przestrzeni
num = 10  # Liczba punktów w siatce
x = np.linspace(-1, 4, num)
y = np.linspace(-1, 4, num)
z = np.linspace(-2, 4, num)
X, Y, Z = np.meshgrid(x, y, z, indexing='ij')  # Ustawienie indeksowania


# Funkcja obliczająca pole elektryczne
def calculate_field(t):
    # Pozycja ładunku w czasie t
    charge_position = np.array([u[0] * t, u[1] * t, u[2] * t])

    # Przesunięcie siatki względem ładunku
    Rx = X - charge_position[0]
    Ry = Y - charge_position[1]
    Rz = Z - charge_position[2]

    # Odległość od ładunku
    r = np.sqrt(Rx ** 2 + Ry ** 2 + Rz ** 2) + 1e-6  # Unikamy dzielenia przez zero

    # Wektor jednostkowy
    R_hat = np.stack((Rx / r, Ry / r, Rz / r), axis=-1)

    # Pole elektryczne (przybliżony model dipola)
    cross1 = np.cross(R_hat, a, axisa=-1, axisb=0)
    E_radiation = (q / (4 * np.pi * epsilon_0)) * np.cross(cross1, R_hat, axisa=-1, axisb=-1) / (
                c ** 2 * r[..., np.newaxis])
    return E_radiation[..., 0], E_radiation[..., 1], E_radiation[..., 2], charge_position


# Tworzenie figury 3D
fig = plt.figure(figsize=(10, 10))
ax = fig.add_subplot(111, projection='3d')
ax.set_xlim(-1, 4)
ax.set_ylim(-1, 4)
ax.set_zlim(-2, 4)
ax.set_xlabel("X")
ax.set_ylabel("Y")
ax.set_zlabel("Z")
ax.set_title("Animacja pola elektrycznego wytworzonego przez poruszający się ładunek")

# Wektory pola elektrycznego
quiver = None
charge_marker, = ax.plot([], [], [], 'ro', markersize=8, label="Ładunek")  # Czerwony punkt dla ładunku


def update(frame):
    global quiver
    if quiver:
        quiver.remove()  # Usunięcie poprzednich wektorów
    t = frame * 0.1  # Czas
    Ex, Ey, Ez, charge_position = calculate_field(t)

    # Renderowanie pola elektrycznego
    quiver = ax.quiver(X, Y, Z, Ex, Ey, Ez, length=0.5, normalize=True, color='blue')

    # Aktualizacja pozycji markera ładunku
    charge_marker.set_data([charge_position[0]], [charge_position[1]])
    charge_marker.set_3d_properties([charge_position[2]])


# Tworzenie animacji
ani = FuncAnimation(fig, update, frames=100, interval=50)

# Dodanie legendy
ax.legend()

plt.show()