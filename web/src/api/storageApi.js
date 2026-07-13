import { supabase } from './supabaseClient'

export async function uploadPartImage(file) {
  const fileExt = file.name.split('.').pop()
  const fileName = `${Date.now()}-${Math.random().toString(36).slice(2)}.${fileExt}`

  const { error: uploadError } = await supabase.storage
    .from('part-images')
    .upload(fileName, file, { upsert: false })

  if (uploadError) {
    throw new Error(uploadError.message)
  }

  const { data } = supabase.storage
    .from('part-images')
    .getPublicUrl(fileName)

  return data.publicUrl
}
