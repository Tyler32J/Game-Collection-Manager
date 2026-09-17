// useState lets a component remember values between renders.
// useEffect lets us run code whenever certain values change (like a class on <html>).
import { useState, useEffect } from 'react'
import { Search, Plus, Sun, Moon } from 'lucide-react'
import GameCard from './GameCard'
import GameModal from './GameModal'

// Starting sample games so the app isn't empty on first load
const initialGames = [
  {
    id: 1,
    title: 'The Lord of Rings Battle for Middle-Earth',
    platform: 'PC',
    genre: 'Strategy',
    status: 'Completed',
    rating: 5,
    coverImage: '/the_lord_of_rings_battle_of_middle_earth_1.png',
  },
  {
    id: 2,
    title: 'Halo 3',
    platform: 'Xbox 360',
    genre: 'Action',
    status: 'Playing',
    rating: 4,
    coverImage: '/halo_3.png',
  },
  {
    id: 3,
    title: 'Turok',
    platform: 'PlayStation 2',
    genre: 'Action',
    status: 'Abandoned',
    rating: 3,
    coverImage: '/turok.png',
  },
  {
    id: 4,
    title: 'Rise of the Witch-King',
    platform: 'PC',
    genre: 'Strategy',
    status: 'Completed',
    rating: 5,
    coverImage: '/the_lord_of_rings_battle_of_middle_earth_3.png',
  },
  {
    id: 5,
    title: 'Star Wars Battlefront',
    platform: 'PlayStation 2',
    genre: 'Action',
    status: 'Completed',
    rating: 4,
    coverImage: '/star_wars_battlefront.png',
  },
  {
    id: 6,
    title: 'Cabelas Outdoor Adventures',
    platform: 'PlayStation 3',
    genre: 'Action',
    status: 'Completed',
    rating: 4,
    coverImage: '/cabelas_outdoor_adventures.png',
  },
  {
    id: 7,
    title: 'Mario Kart',
    platform: 'Wii',
    genre: 'Action',
    status: 'Completed',
    rating: 4,
    coverImage: '/mariokart.png',
  },
  {
    id: 8,
    title: 'Call of Duty Black OPs 2',
    platform: 'Xbox 360',
    genre: 'Action',
    status: 'Completed',
    rating: 4,
    coverImage: '/call_of_duty_black_ops_2.png',
  },
  {
    id: 9,
    title: 'Minecraft',
    platform: 'Xbox One',
    genre: 'Action',
    status: 'Abandoned',
    rating: 2,
    coverImage: '/minecraft.png',
  },
]

// Options shown in the filter dropdowns above the game grid
const PLATFORM_FILTERS = [
  'All Platforms',
  'Nintendo Switch',
  'Nintendo 64',
  'GameCube',
  'Wii',
  'Wii U',
  'PlayStation 1',
  'PlayStation 2',
  'PlayStation 3',
  'PlayStation 4',
  'PlayStation 5',
  'Xbox (Original)',
  'Xbox 360',
  'Xbox One',
  'PC',
]
const STATUS_FILTERS = ['All Status', 'Playing', 'Completed', 'Abandoned']

function App() {
  // ---------- STATE ----------
  // "games" holds every game in the collection. Whenever we call setGames,
  // React re-renders the page with the new list.
  const [games, setGames] = useState(initialGames)

  // These three hold whatever the user has typed/picked in the search box
  // and the two filter dropdowns.
  const [searchText, setSearchText] = useState('')
  const [platformFilter, setPlatformFilter] = useState('All Platforms')
  const [statusFilter, setStatusFilter] = useState('All Status')

  // Controls whether the Add/Edit modal is showing, and which game (if any)
  // is being edited. editingGame stays null when we're adding a brand new game.
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [editingGame, setEditingGame] = useState(null)

  // Current color theme. We read any previously saved choice from
  // localStorage first, so the theme persists after a page refresh.
  // If nothing was saved yet, default to 'light'.
  const [theme, setTheme] = useState(() => localStorage.getItem('theme') || 'light')

  // Runs every time "theme" changes. Tailwind's dark: classes only turn on
  // when an ancestor element has the class "dark" — so we add/remove that
  // class on <html> here, and remember the choice for next time.
  useEffect(() => {
    document.documentElement.classList.toggle('dark', theme === 'dark')
    localStorage.setItem('theme', theme)
  }, [theme])

  // Flips between 'light' and 'dark' each time the button is clicked
  function toggleTheme() {
    setTheme(theme === 'light' ? 'dark' : 'light')
  }

  // ---------- DERIVED DATA ----------
  // "Derived" means we don't store this in state — we just recalculate it
  // every render from the games/search/filter values above.
  const filteredGames = games.filter((game) => {
    const matchesSearch = game.title.toLowerCase().includes(searchText.toLowerCase())
    const matchesPlatform = platformFilter === 'All Platforms' || game.platform === platformFilter
    const matchesStatus = statusFilter === 'All Status' || game.status === statusFilter
    // A game only shows up if it satisfies ALL three conditions
    return matchesSearch && matchesPlatform && matchesStatus
  })

  // Numbers shown in the stat cards at the top of the page
  const totalGames = games.length
  const completedCount = games.filter((g) => g.status === 'Completed').length
  const playingCount = games.filter((g) => g.status === 'Playing').length
  const abandonedCount = games.filter((g) => g.status === 'Abandoned').length

  // ---------- EVENT HANDLERS ----------
  // Opens the modal in "add" mode (no game passed in = a blank form)
  function openAddModal() {
    setEditingGame(null)
    setIsModalOpen(true)
  }

  // Opens the modal in "edit" mode, pre-filled with the clicked game's data
  function openEditModal(game) {
    setEditingGame(game)
    setIsModalOpen(true)
  }

  function closeModal() {
    setIsModalOpen(false)
    setEditingGame(null)
  }

  // Called by the modal when the user submits the form.
  // If we were editing a game, replace it in the list; otherwise add it as new.
  function handleSaveGame(game) {
    if (editingGame) {
      setGames(games.map((g) => (g.id === game.id ? game : g)))
    } else {
      setGames([...games, game])
    }
    closeModal()
  }

  // Removes a game from the list by filtering it out (keeps everything else)
  function handleDeleteGame(id) {
    setGames(games.filter((g) => g.id !== id))
  }

  // ---------- UI ----------
  return (
    // "dark:" classes here only apply once <html> has the "dark" class,
    // which the useEffect above toggles for us.
    <div className="min-h-screen bg-white dark:bg-gray-900 transition-colors">
      <div className="max-w-7xl mx-auto px-6 py-8">

        {/* Page title on the left, theme toggle button on the right */}
        <header className="mb-8 flex items-start justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Game Collection Manager</h1>
            <p className="text-gray-500 dark:text-gray-400 mt-1">Track and organize your video game library</p>
          </div>

          <button
            onClick={toggleTheme}
            aria-label="Toggle light and dark theme"
            className="p-2 rounded-md border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-800"
          >
            {/* Show a moon icon in light mode (click to go dark) and a sun icon in dark mode (click to go light) */}
            {theme === 'light' ? <Moon size={20} /> : <Sun size={20} />}
          </button>
        </header>

        {/* Stat cards */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4">
            <p className="text-sm text-gray-500 dark:text-gray-400">Total Games</p>
            <p className="text-2xl font-bold text-gray-900 dark:text-white">{totalGames}</p>
          </div>
          <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4">
            <p className="text-sm text-gray-500 dark:text-gray-400">Completed</p>
            <p className="text-2xl font-bold text-green-600 dark:text-green-400">{completedCount}</p>
          </div>
          <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4">
            <p className="text-sm text-gray-500 dark:text-gray-400">Playing</p>
            <p className="text-2xl font-bold text-blue-600 dark:text-blue-400">{playingCount}</p>
          </div>
          <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4">
            <p className="text-sm text-gray-500 dark:text-gray-400">Abandoned</p>
            <p className="text-2xl font-bold text-red-600 dark:text-red-400">{abandonedCount}</p>
          </div>
        </div>

        {/* Search box + filter dropdowns + Add Game button */}
        <div className="flex flex-col sm:flex-row gap-3 mb-8">
          <div className="relative flex-1">
            <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              placeholder="Search games..."
              className="w-full bg-gray-100 dark:bg-gray-800 dark:text-white rounded-md pl-10 pr-4 py-2 outline-none border-2 border-transparent hover:border-red-600 focus:ring-2 focus:ring-gray-300 dark:focus:ring-gray-600"
            />
          </div>

          <select
            value={platformFilter}
            onChange={(e) => setPlatformFilter(e.target.value)}
            className="bg-white dark:bg-gray-800 dark:text-white border-2 border-gray-300 dark:border-gray-600 hover:border-red-600 rounded-md px-3 py-2"
          >
            {PLATFORM_FILTERS.map((p) => (
              <option key={p} value={p}>{p}</option>
            ))}
          </select>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="bg-white dark:bg-gray-800 dark:text-white border-2 border-gray-300 dark:border-gray-600 hover:border-red-600 rounded-md px-3 py-2"
          >
            {STATUS_FILTERS.map((s) => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>

          <button
            onClick={openAddModal}
            className="flex items-center justify-center gap-2 bg-black hover:bg-gray-800 dark:bg-white dark:text-black dark:hover:bg-gray-200 text-white font-medium rounded-md px-4 py-2 whitespace-nowrap border-2 border-transparent hover:border-red-600"
          >
            <Plus size={18} />
            Add Game
          </button>
        </div>

        {/* The grid of game cards. We map over filteredGames (not the full
            games list) so the search/filter controls above actually work. */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {filteredGames.map((game) => (
            <GameCard
              key={game.id}
              game={game}
              onEdit={openEditModal}
              onDelete={handleDeleteGame}
            />
          ))}
        </div>

        {/* Friendly message when filters/search leave nothing to show */}
        {filteredGames.length === 0 && (
          <p className="text-center text-gray-500 dark:text-gray-400 mt-12">No games match your search or filters.</p>
        )}
      </div>

      {/* The modal only exists in the DOM while isModalOpen is true */}
      {isModalOpen && (
        <GameModal game={editingGame} onClose={closeModal} onSave={handleSaveGame} />
      )}
    </div>
  )
}

export default App
