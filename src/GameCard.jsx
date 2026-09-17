import { useState } from 'react'
import { Star, Pencil, Trash2, ImageOff } from 'lucide-react'

// Background/text color for each possible status badge.
// Looking these up by key (statusColors[game.status]) avoids a big
// if/else chain further down.
const statusColors = {
  Backlog: 'bg-gray-100 text-gray-700',
  Playing: 'bg-blue-600 text-white',
  Completed: 'bg-green-600 text-white',
  Abandoned: 'bg-red-600 text-white',
}

// One card for one game. It just displays data it's given (via props)
// and calls onEdit/onDelete (functions passed down from App.jsx) when
// its buttons are clicked — it doesn't manage any state itself.
function GameCard({ game, onEdit, onDelete }) {
  // Tracks whether the cover image URL failed to load, so we can show a
  // clean placeholder instead of the browser's default broken-image icon.
  const [imageFailed, setImageFailed] = useState(false)

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 overflow-hidden flex flex-col">
      {/* Cover image with the status badge floating on top of it.
          bg-gray-100/900 shows through as padding on the sides when an
          image's proportions don't exactly match the box (see object-contain below). */}
      <div className="relative h-72 bg-gray-100 dark:bg-gray-900">
        {imageFailed ? (
          <div className="w-full h-full flex flex-col items-center justify-center gap-2 text-gray-400">
            <ImageOff size={32} />
            <span className="text-sm">Image not found</span>
          </div>
        ) : (
          <img
            src={game.coverImage}
            alt={game.title}
            onError={() => setImageFailed(true)}
            // object-contain shrinks the image to fit entirely inside the box
            // (no cropping), instead of stretching or cutting off the edges.
            className="w-full h-full object-contain"
          />
        )}
        <span
          className={`absolute top-3 right-3 px-3 py-1 rounded-full text-sm font-medium ${statusColors[game.status]}`}
        >
          {game.status}
        </span>
      </div>

      <div className="p-4 flex flex-col flex-1">
        <h3 className="font-bold text-lg text-gray-900 dark:text-white truncate">{game.title}</h3>
        <p className="text-gray-500 dark:text-gray-400 text-sm">{game.platform}</p>
        <p className="text-gray-500 dark:text-gray-400 text-sm mb-2">{game.genre}</p>

        {/* Draw 5 stars total; fill in the ones at or below the game's rating */}
        <div className="flex mb-4">
          {[1, 2, 3, 4, 5].map((n) => (
            <Star
              key={n}
              size={18}
              className={n <= game.rating ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300 dark:text-gray-600'}
            />
          ))}
        </div>

        {/* mt-auto pushes these buttons to the bottom of the card even if
            the text above is shorter on some cards than others */}
        <div className="flex gap-2 mt-auto">
          <button
            onClick={() => onEdit(game)}
            className="flex-1 flex items-center justify-center gap-1 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 text-gray-900 dark:text-white font-medium rounded-md py-2 border-2 border-transparent hover:border-red-600"
          >
            <Pencil size={16} />
            Edit
          </button>
          <button
            onClick={() => onDelete(game.id)}
            className="bg-red-600 hover:bg-red-700 text-white rounded-md px-3 flex items-center justify-center"
          >
            <Trash2 size={18} />
          </button>
        </div>
      </div>
    </div>
  )
}

export default GameCard
