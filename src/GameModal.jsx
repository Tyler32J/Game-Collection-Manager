import { useState, useRef } from 'react'
import { X, Upload, Star } from 'lucide-react'

const PLATFORMS = [
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
const GENRES = [
  'Adventure',
  'RPG',
  'Action',
  'Strategy',
  'Shooter',
  'Sports',
  'Racing',
  'Simulation',
  'Puzzle',
  'Horror',
  'Platformer',
  'Survival',
  'Sandbox/Open World',
]

// can only be set to Playing, Completed, or Abandoned.
const STATUSES = ['Playing', 'Completed', 'Abandoned']

// Shared Tailwind classes for every text/select input in this form,
// so we only have to write the light/dark styling once.
// border-2 border-transparent keeps the layout steady, then hover:border-red-600
// swaps in a solid red outline (border-transparent -> border-red-600 on hover).
const inputClasses =
  'w-full bg-gray-100 dark:bg-gray-700 dark:text-white rounded-md px-3 py-2 outline-none border-2 border-transparent hover:border-red-600 focus:ring-2 focus:ring-gray-300 dark:focus:ring-gray-500'

// This component is used for BOTH adding a new game and editing an
// existing one. If a "game" prop is passed in, we're editing and the
// fields start pre-filled with its data; otherwise we start blank.
function GameModal({ game, onClose, onSave }) {
  const isEditing = Boolean(game)

  // One piece of state per form field. useState(initialValue) gives us
  // back [currentValue, functionToUpdateIt].
  const [title, setTitle] = useState(game ? game.title : '')
  const [platform, setPlatform] = useState(game ? game.platform : '')
  const [genre, setGenre] = useState(game ? game.genre : '')
  const [status, setStatus] = useState(game ? game.status : STATUSES[0])
  const [rating, setRating] = useState(game ? game.rating : 0)
  const [coverImage, setCoverImage] = useState(game ? game.coverImage : '')

  // A ref lets us "reach into" a real DOM element directly. We use it here
  // to click the hidden file input from our own custom-styled button.
  const fileInputRef = useRef(null)

  // Runs when the user picks a file in the file explorer dialog
  function handleFileChange(e) {
    const file = e.target.files[0]
    if (!file) return
    // URL.createObjectURL turns the picked file into a temporary link the
    // browser can show in an <img>, with no upload or server needed.
    setCoverImage(URL.createObjectURL(file))
  }

  // Runs when the form is submitted (Enter key or the submit button)
  function handleSubmit(e) {
    e.preventDefault() // stops the browser from reloading the page
    if (!title || !platform || !genre) return // simple required-field check

    // Hand the finished game object back up to App.jsx, which decides
    // whether to add it as new or update the existing one.
    onSave({
      id: game ? game.id : Date.now(), // reuse the id when editing, make a new one otherwise
      title,
      platform,
      genre,
      status,
      rating: Number(rating), // number inputs give back strings, so convert it
      coverImage,
    })
  }

  return (
    // Full-screen dark overlay behind the modal
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
      <div className="bg-white dark:bg-gray-800 rounded-lg w-full max-w-md max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          <h2 className="text-xl font-bold text-gray-900 dark:text-white">
            {isEditing ? 'Edit Game' : 'Add New Game'}
          </h2>
          <button onClick={onClose} className="p-2 -m-2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-200">
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="px-6 py-4 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-900 dark:text-gray-200 mb-1">Title *</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
              className={inputClasses}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-900 dark:text-gray-200 mb-1">Platform *</label>
            <select
              value={platform}
              onChange={(e) => setPlatform(e.target.value)}
              required
              className={inputClasses}
            >
              <option value="" disabled>Select Platform</option>
              {/* Turn the PLATFORMS array into one <option> per entry */}
              {PLATFORMS.map((p) => (
                <option key={p} value={p}>{p}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-900 dark:text-gray-200 mb-1">Genre *</label>
            <select
              value={genre}
              onChange={(e) => setGenre(e.target.value)}
              required
              className={inputClasses}
            >
              <option value="" disabled>Select Genre</option>
              {GENRES.map((g) => (
                <option key={g} value={g}>{g}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-900 dark:text-gray-200 mb-1">Status *</label>
            <select
              value={status}
              onChange={(e) => setStatus(e.target.value)}
              required
              className={inputClasses}
            >
              {STATUSES.map((s) => (
                <option key={s} value={s}>{s}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-900 dark:text-gray-200 mb-1">Rating (1-5)</label>
            {/* Five clickable stars instead of a number box — clicking star
                "n" sets the rating to n. Clicking the currently-selected
                star again resets the rating back to 0. */}
            <div className="flex gap-1">
              {[1, 2, 3, 4, 5].map((n) => (
                <button
                  key={n}
                  type="button"
                  onClick={() => setRating(n === rating ? 0 : n)}
                  aria-label={`Rate ${n} star${n > 1 ? 's' : ''}`}
                  className="p-2 -m-1"
                >
                  <Star
                    size={26}
                    className={n <= rating ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300 dark:text-gray-600'}
                  />
                </button>
              ))}
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-900 dark:text-gray-200 mb-1">Cover Image</label>

            {/* The real file input is invisible — clicking our styled
                button below triggers it via fileInputRef instead. */}
            <input
              type="file"
              accept="image/*"
              ref={fileInputRef}
              onChange={handleFileChange}
              className="hidden"
            />

            <button
              type="button"
              onClick={() => fileInputRef.current.click()}
              className="w-full rounded-md border-2 border-dashed border-gray-300 dark:border-gray-600 hover:border-solid hover:border-red-600 dark:hover:border-red-600 overflow-hidden"
            >
              {coverImage ? (
                <img src={coverImage} alt="Cover preview" className="w-full h-32 object-cover" />
              ) : (
                <div className="flex flex-col items-center justify-center gap-1 py-6 text-gray-400">
                  <Upload size={20} />
                  <span className="text-sm">Click to choose an image</span>
                </div>
              )}
            </button>
          </div>

          {/* Cancel just closes the modal (type="button" stops it from submitting the form).
              The other button's type="submit" triggers handleSubmit above. */}
          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 rounded-md border-2 border-transparent hover:border-red-600 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 text-gray-900 dark:text-white font-medium"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 rounded-md border-2 border-transparent hover:border-red-600 bg-black hover:bg-gray-800 dark:bg-white dark:text-black dark:hover:bg-gray-200 text-white font-medium"
            >
              {isEditing ? 'Save Changes' : 'Add Game'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default GameModal
